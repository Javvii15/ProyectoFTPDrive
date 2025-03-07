📌 Descripción
Este proyecto implementa un sistema de sincronización de archivos entre una carpeta local y un servidor FTP que es la carpeta reomata. Además, los archivos se cifran con AES antes de ser subidos y se descifran al ser descargados.

🚀 Características
- Sincronización automática entre una carpeta local y el servidor FTP.
- Cifrado y descifrado de archivos utilizando AES para mayor seguridad.
- Utilizo programación concurrente con múltiples hilos.

📁 Estructura del Proyecto
EjercicioA
 ├── Sincronizar.java   -   Sincroniza archivos entre local y FTP usando múltiples hilos.
 ├── SeguridadAES.java  -   Métodos para cifrar y descifrar archivos con AES.
 ├── AESSimpleManager.java -Gestión de la clave y cifrado/descifrado AES.
 ├── DescargarDescifrar.java # Descarga y descifra archivos del servidor FTP.

⚙️ Funcionalidad

Sincronizar.java
Obtiene los archivos locales y los compara con los archivos en el FTP.
Sube archivos nuevos o modificados al servidor, cifrándolos con AES.
Usa multihilos para optimizar el proceso.

SeguridadAES.java y AESSimpleManager.java
Los archivos subidos al servidor se cifran con AES/ECB/PKCS5Padding.
Se usa una clave secreta definida en el código.
Los archivos descargados se descifran automáticamente.
 
DescargarDescifrar.java
Conecta al FTP y descarga los archivos cifrados.
Descifra cada archivo y lo guarda en la carpeta local elegida.

🔧 Requisitos Previos
  Tener un servidor FTP funcional en mi caso con xampp filezilla (me ha dado muchos problemas).
  Configurar usuario y contraseña en Sincronizar.java y DescargarDescifrar.java.
  Biblioteca externa: org.apache.commons.net para la conexión FTP.

🛠 Mejoras Futuras
  Implementar un log detallado de cada sincronización.
