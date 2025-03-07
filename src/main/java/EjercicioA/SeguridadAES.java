package EjercicioA;

import java.io.*;
import java.nio.file.Files;
import java.security.Key;
public class SeguridadAES {

    private static final String PASSWORD = "NoMeLlamoSpidermanMeLlamoSpidermanMeLlamoSpiderman";
    private static final int LONGITUD_BLOQUE = 32;

    public static void cifrarArchivo(File archivoEntrada, File archivoSalida) throws Exception {
        Key clave = AESSimpleManager.obtenerClave(PASSWORD, LONGITUD_BLOQUE);
        String contenido = new String(Files.readAllBytes(archivoEntrada.toPath()));
        String contenidoCifrado = AESSimpleManager.cifrar(contenido, clave);
        Files.write(archivoSalida.toPath(), contenidoCifrado.getBytes());
    }

    public static void descifrarArchivo(File archivoEntrada, File archivoSalida) throws Exception {
        Key clave = AESSimpleManager.obtenerClave(PASSWORD, LONGITUD_BLOQUE);
        String contenidoCifrado = new String(Files.readAllBytes(archivoEntrada.toPath()));
        String contenidoDescifrado = AESSimpleManager.descifrar(contenidoCifrado, clave);
        Files.write(archivoSalida.toPath(), contenidoDescifrado.getBytes());
    }
}
