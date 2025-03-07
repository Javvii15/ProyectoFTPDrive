package EjercicioA;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sincronizar {

    private static final String SERVIDOR_FTP = "127.0.0.1";
    private static final String USUARIO = "Javier";
    private static final String CONTRASENA = "1234";
    private static final String CARPETA_LOCAL = "C:\\Users\\Mediamarkt nevada\\Desktop\\carpetalocal";
    private static final String CARPETA_REMOTA = "prueba";
    private static final long TIEMPO_REFRESCO = 3000;

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        while (true) {
            executor.execute(() -> {
                try {
                    sincronizar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            try {
                Thread.sleep(TIEMPO_REFRESCO);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sincronizar() throws IOException {
        FTPClient clienteFTP = new FTPClient();
        clienteFTP.connect(SERVIDOR_FTP);

        boolean loginExitoso = clienteFTP.login(USUARIO, CONTRASENA);
        if (!loginExitoso) {
            System.out.println("Error: No se pudo iniciar sesión en el servidor FTP.");
            clienteFTP.disconnect();
            return;
        }
        List<String> archivosRemotos = obtenerArchivosRemotos(clienteFTP, CARPETA_REMOTA);
        List<String> archivosLocales = obtenerArchivosLocales(CARPETA_LOCAL);

        for (String archivoRemoto : archivosRemotos) {
            if (!archivosLocales.contains(archivoRemoto.replace(".aes", ""))) {
                borrarArchivo(clienteFTP, archivoRemoto);
            }
        }

        for (String archivoLocal : archivosLocales) {
            if (!archivosRemotos.contains(archivoLocal + ".aes") || estaArchivoActualizado(clienteFTP, CARPETA_REMOTA, archivoLocal + ".aes", new File(CARPETA_LOCAL + File.separator + archivoLocal).lastModified())) {
                añadirArchivo(clienteFTP, archivoLocal);
            }
        }

        clienteFTP.disconnect();
    }

    private static List<String> obtenerArchivosRemotos(FTPClient clienteFTP, String carpeta) throws IOException {
        List<String> archivos = new ArrayList<>();
        clienteFTP.changeWorkingDirectory(carpeta);
        for (FTPFile archivo : clienteFTP.listFiles()) {
            if (archivo.isFile()) {
                archivos.add(archivo.getName());
            }
        }
        return archivos;
    }

    private static List<String> obtenerArchivosLocales(String carpeta) {
        List<String> archivos = new ArrayList<>();
        File directorio = new File(carpeta);
        File[] listaArchivos = directorio.listFiles();
        if (listaArchivos != null) {
            for (File archivo : listaArchivos) {
                if (archivo.isFile()) {
                    archivos.add(archivo.getName());
                }
            }
        }
        return archivos;
    }

    private static void añadirArchivo(FTPClient clienteFTP, String archivo) throws IOException {
        File localFile = new File(CARPETA_LOCAL + File.separator + archivo);
        File archivoCifrado = new File(CARPETA_LOCAL + File.separator + archivo + ".aes");

        try {
            SeguridadAES.cifrarArchivo(localFile, archivoCifrado);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        FileInputStream fisCifrado = new FileInputStream(archivoCifrado);
        clienteFTP.storeFile(archivo + ".aes", fisCifrado);
        fisCifrado.close();
        System.out.println("Archivo " + archivo + ".aes cifrado y subido correctamente.");

        archivoCifrado.delete();
    }
    
    private static void borrarArchivo(FTPClient clienteFTP, String archivo) throws IOException {
        clienteFTP.deleteFile(archivo);
        System.out.println("Archivo " + archivo + " eliminado del servidor remoto.");
    }

    private static boolean estaArchivoActualizado(FTPClient clienteFTP, String carpetaRemota, String nombreArchivo, long ultimaModificacionLocal) throws IOException {
        clienteFTP.changeWorkingDirectory(carpetaRemota);
        FTPFile[] archivosRemotos = clienteFTP.listFiles();

        for (FTPFile archivoRemoto : archivosRemotos) {
            if (archivoRemoto.getName().equals(nombreArchivo)) {
                long ultimaModificacionRemota = archivoRemoto.getTimestamp().getTimeInMillis();
                return ultimaModificacionLocal > ultimaModificacionRemota;
            }
        }

        return false;
    }
}