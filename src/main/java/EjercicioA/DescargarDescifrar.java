package EjercicioA;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class DescargarDescifrar {

    private static final String SERVIDOR_FTP = "127.0.0.1";
    private static final String USUARIO = "Javier";
    private static final String CONTRASENA = "1234";
    private static final String CARPETA_REMOTA = "antonio";

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        FTPClient clienteFTP = new FTPClient();
        try {
            clienteFTP.connect(SERVIDOR_FTP);
            boolean loginExitoso = clienteFTP.login(USUARIO, CONTRASENA);
            if (!loginExitoso) {
                System.out.println("Error: No se pudo iniciar sesión en el servidor FTP.");
                return;
            }

            System.out.print("Ingresa la ruta de la carpeta local donde quieres guardar los archivos descifrados: ");
            String carpetaLocal = teclado.nextLine();

            File carpeta = new File(carpetaLocal);
            if (!carpeta.exists()) {
                System.out.println("La carpeta local no existe. Creando carpeta");
                if (!carpeta.mkdirs()) {
                    System.out.println("Error: No se pudo crear la carpeta local.");
                    return;
                }
            }

            clienteFTP.changeWorkingDirectory(CARPETA_REMOTA);
            FTPFile[] archivosRemotos = clienteFTP.listFiles();
            for (FTPFile archivoRemoto : archivosRemotos) {
                if (archivoRemoto.isFile() && archivoRemoto.getName().endsWith(".aes")) {
                    descargarYDescifrarArchivo(clienteFTP, archivoRemoto.getName(), carpetaLocal);
                }
            }

            System.out.println("Todos los archivos han sido descargados y descifrados correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteFTP.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void descargarYDescifrarArchivo(FTPClient clienteFTP, String archivoRemoto, String carpetaLocal) throws IOException {
        File archivoCifrado = new File(carpetaLocal + File.separator + archivoRemoto);
        File archivoDescifrado = new File(carpetaLocal + File.separator + archivoRemoto.replace(".aes", ""));

        try (FileOutputStream fos = new FileOutputStream(archivoCifrado)) {
            boolean descargado = clienteFTP.retrieveFile(archivoRemoto, fos);
            if (descargado) {
                System.out.println("Archivo " + archivoRemoto + " descargado correctamente.");
            } else {
                System.out.println("Error al descargar el archivo " + archivoRemoto);
                return;
            }
        }

        try {
            SeguridadAES.descifrarArchivo(archivoCifrado, archivoDescifrado);
            System.out.println("Archivo descifrado y guardado en: " + archivoDescifrado.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        archivoCifrado.delete();
    }
}