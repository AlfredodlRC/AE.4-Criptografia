import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import modelo.Coche;

public class Principal {

	static Scanner lector = new Scanner(System.in);


	static byte[] bytesFraseCifrada;

	
	static KeyGenerator generadorSimetrico;
	static KeyPairGenerator generadorAsimetrico;

	static SecretKey claveSimetrica;
	static KeyPair clavesAsimetricas;

	static Cipher codificador = null;

	
	
	public static void main(String[] args) {
		
		boolean continuar = true;
		String opcion;
		
		try {
			generadorSimetrico = KeyGenerator.getInstance("AES");
			claveSimetrica = generadorSimetrico.generateKey();
		} catch (NoSuchAlgorithmException e2) {
			System.out.println("Error al crear la instancia de clave simetrica");
			return;
		} catch (Exception e) {
			System.out.println("Error al crear la  clave simetrica");
			return;
		}

		try {
			generadorAsimetrico = KeyPairGenerator.getInstance("RSA");
			clavesAsimetricas = generadorAsimetrico.generateKeyPair();
		} catch (NoSuchAlgorithmException e1) {
			System.out.println("Error al crear la clave asimetrica");			
			return;
		} catch (Exception e) {
			System.out.println("Error al crear las claves asimetricas");			
			return;
		}
		

		
		while (continuar == true) {
			System.out.println("1- Criptografia simetrica");
			System.out.println("2- Criptografia asimetrica");
			System.out.println("3- Salir");
			opcion = lector.nextLine();
			switch (opcion) {
			case "1":
				CriptografiaSimetrica();
				break;
			case "2":
				CriptografiaAsimetrica();
				break;				
			case "3":
				continuar = false;
				break;
			default:
			}
		}
	}

	/*
	 * Función que gestiona el menu de la codificación simetrica
	 */
private static  void CriptografiaSimetrica() {
		boolean continuar = true;
		String opcion;
		
		String frase = "";
		Coche cocheIntroducido;
		
		/*
		 * Creamos el codificado como una instacia de un algoritmo simetrico
		 */
		try {
			codificador = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error al crear la instancia codificadora");
			return;
		}

		/*
		 * bucle del menu de codificación asimetrica
		 */
		while (continuar == true) {			
			System.out.println("   Codificacion simetrica");
			System.out.println("----------------------------");
			mostrarMenu();		
			opcion = lector.nextLine();
			switch (opcion) {
				case "1"://Con la opción 1 el programa le pedirá al usuario una frase, la encriptará y la guardará en memoria.
					System.out.println("Introduce la frase a codificar");		
					frase = lector.nextLine();		
					codificarSimetrica(frase);
					frase = "Frase codificada.";
					break;
				case "2"://Con la opción 2 el programa mostrará la frase encriptada (no debería ser legible)
					frase = mostrarFraseEncriptada();
					break;				
				case "3"://Con la opción 3 el programa mostrará la frase desencriptándola.
					frase = decodificarSimetrica();
					break;
				case "4"://Encriptar Coche
					cocheIntroducido = IntroducirCoche();
					frase = codificarCocheSimetrico(cocheIntroducido);
					break;				
				case "5": // Salimos del submenu simetrico anulando el codificador y la frase encriptada
					codificador = null;
					bytesFraseCifrada = null;
					continuar = false;
					frase = "Volviendo al menu principal";
					break;
				default:
					frase = "Opción no contemplada";
			}
			System.out.println(frase);
			frase = "";
		}

	}

	/*
	 * Función que gestiona el menu de la codificación asimetrica
	 */
	private static void CriptografiaAsimetrica() {

		boolean continuar = true;
		String opcion;
		String frase = "";
		
		Coche cocheIntroducido;
		SealedObject cocheCodificado = null;

		/*
		 * Creamos el codificado como una instacia de un algoritmo asimetrico
		 */
		try {
			codificador = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error al crear la instancia codificadora");
			return;
		}

		/*
		 * bucle del menu de codificación asimetrica
		 */
		while (continuar == true) {
			System.out.println("   Codificacion asimetrica");
			System.out.println("-----------------------------");
			mostrarMenu();		
			opcion = lector.nextLine();
			switch (opcion) {
				case "1"://Con la opción 1 el programa le pedirá al usuario una frase, la encriptará y la guardará en memoria.
					System.out.println("Introduce la frase a encriptar");		
					frase = lector.nextLine();		
					frase = codificarAsimetrica(frase);
					break;
				case "2"://Con la opción 2 el programa mostrará la frase encriptada (no debería ser legible)
					frase = mostrarFraseEncriptada();
					break;				
				case "3"://Con la opción 3 el programa mostrará la frase desencriptándola.
					frase = decodificarAsimetrica();
					break;
				case "4"://Encriptar Coche
					cocheIntroducido = IntroducirCoche();
					frase = codificarCocheASimetrico(cocheIntroducido);
					break;				
				case "5": // Salimos del submenu asimetrico anulando el codificador y la frase encriptada
					codificador = null;
					bytesFraseCifrada = null;
					continuar = false;
					frase = "Volviendo al menu principal";
					break;
				default:
					frase = "Opción no contemplada";
				}
			System.out.println(frase);
			frase = "";

		}

	}
	
	/*
	 * función para mostrar el menu
	 */
	private static void mostrarMenu() {
		System.out.println("1- Encriptar frase");
		System.out.println("2- Mostrar frase encriptada");
		System.out.println("3- Desencriptar frase");
		System.out.println("4- Encriptar Coche");
		System.out.println("5- Volver al menu principal");		
	}

	/*
	 * Función para pedir los datos del coche a codificar
	 */
	private static Coche IntroducirCoche() {
		
		Coche resultado = new Coche();
		
		String matricula;		
		String marca;		
		String modelo;		
		String precio;
		
		/*
		 * Pedimos los datos del coche  
		 */
		System.out.println("  Introducir Coche");
		System.out.println("--------------------");

		System.out.println("Introduce la matricula:");
		matricula = lector.nextLine();
		System.out.println("Introduce la marca:");
		marca = lector.nextLine();
		System.out.println("Introduce el modelo:");
		modelo = lector.nextLine();	
		System.out.println("Introduce el precio:");
		precio = lector.nextLine();

		/*
		 * Creamos el coche y lo retornmamos
		 */
		resultado = new Coche(matricula, marca, modelo, precio);
		return resultado;
	}
	
	/*
	 * Función para realizar la codificación simetrica del parametro frase
	 */
	private static String codificarSimetrica(String frase) {

		byte[] bytesFrase;

		bytesFrase = frase.getBytes(); // Pasamos la frase a un array de bytes
				
		/*
		 * Inicializamos el codificador en modo codificador con la clave privada
		 * y obtenemos los bytes de la frase encriptada (dentro del atributo bytesFraseCifrada)
		 */
		try {
			codificador.init(Cipher.ENCRYPT_MODE, claveSimetrica);
			bytesFraseCifrada = codificador.doFinal(bytesFrase);//codificar el mensaje original
		} catch (InvalidKeyException e) {
			return "Error: la clave no es adecuada.";
		} catch (IllegalBlockSizeException e) {
			return "Error por bloque al codificar la frase por "+ e.getMessage();
		} catch (BadPaddingException e) {
			return "Error (por rellenar bloques) al codificar la frase por "+ e.getMessage();
		}
		
		return "Frase codificada simetricamente";
	}
	
	/*
	 * Función que realiza la decodificación simetrica de la frase  
	 */

	private static String decodificarSimetrica() {
		
		byte[] bytesFrase;
		String resultado = "";

		/*
		 * comprobramos que tenemos una frase encriptada 
		 */
		if (bytesFraseCifrada == null) {
			return "No existe una frase que desecriptar";
		}

		if (bytesFraseCifrada.length == 0) {
			return "No existe una frase que desecriptar";
		}

		/*
		 * Ponemos el codificador en modo decodificador con la clave publica, ya que hemos codificado
		 * con la clave privada y obtenemos los bytes de la decodificación
		 *   
		 */		
		try {
			codificador.init(Cipher.DECRYPT_MODE, claveSimetrica);
			bytesFrase = codificador.doFinal(bytesFraseCifrada);//decodificar el mensaje codificado
		} catch (InvalidKeyException e) {
			System.out.println("Error: la clave no es adecuada.");
			return "";
		} catch (IllegalBlockSizeException e) {
			System.out.println("Error por bloque al codificar la frase por "+ e.getMessage());
			return "";
		} catch (BadPaddingException e) {
			System.out.println("Error (por rellenar bloques) al codificar la frase por "+ e.getMessage());
			return "";
		}

		/*
		 * Convertimos los bytes decodificados a una cadena de texto y la retornamos.
		 */
		resultado = new String(bytesFrase);		
		return resultado;
	}
	
	/*
	 * Función que codifica simetricamente el coche entregado como parametro
	 */
	private static String codificarCocheSimetrico(Coche pCoche) {
		
		String resultado;
		SealedObject cocheCodificado = null;

		/*
		 * Iniciamos el codificador en modo codificador con la clave simetrica
		 * y codificamos el coche codificado
		 */

		try {
			codificador.init(Cipher.ENCRYPT_MODE, claveSimetrica);
			cocheCodificado = new SealedObject(pCoche, codificador);
		} catch (InvalidKeyException e) {
			return "Error: la clave no es adecuada.";
		} catch (IllegalBlockSizeException | IOException e) {
			return "Error al decodificar el coche" + e.getMessage();
		}
		
		/*
		 *  Retornamos el coche introducdo y el coche codificado.
		 */

		resultado = "El coche " + pCoche.toString() + "\n" + "Codificado es " + cocheCodificado.toString();
		return resultado;
		
	}
	
	/* 
	 * Función que realiza la codificación asimetrica de la frase que se entrega como parametro
	*/
	private static String codificarAsimetrica(String frase) {
			
			byte[] bytesFrase;

			bytesFrase = frase.getBytes(); // Pasamos la frase a un array de bytes

			/*
			 * Inicializamos el codificador en modo codificador con la clave privada
			 * y obtenemos los bytes de la frase encriptada (dentro del atributo bytesFraseCifrada)
			 */
			try {
				codificador.init(Cipher.ENCRYPT_MODE, clavesAsimetricas.getPrivate());
				bytesFraseCifrada = codificador.doFinal(bytesFrase);
			} catch (InvalidKeyException e) {
				return "Error: la clave no es adecuada.";
			} catch (IllegalBlockSizeException e) {
				return "Error por bloque al codificar la frase por "+ e.getMessage();
			} catch (BadPaddingException e) {
				return "Error (por rellenar bloques) al codificar la frase por "+ e.getMessage();
			}
			
			return "Frase codificada asimetricamente.";
			
	}

	
	/*
	 * Función que entrega la frase decodificada asimetricamente.
	 */
	private static String decodificarAsimetrica() {
		
		byte[] bytesFrase;
		String resultado = "";

		/*
		 * comprobramos que tenemos una frase encriptada 
		 */
		if (bytesFraseCifrada == null) {
			return "No existe una frase que desecriptar";
		}

		if (bytesFraseCifrada.length == 0) {
			return "No existe una frase que desecriptar";
		}


		/*
		 * Ponemos el codificador en modo decodificador con la clave publica, ya que hemos codificado
		 * con la clave privada y obtenemos los bytes de la decodificación
		 *   
		 */
		try {
			codificador.init(Cipher.DECRYPT_MODE, clavesAsimetricas.getPublic());
			bytesFrase = codificador.doFinal(bytesFraseCifrada);
		} catch (InvalidKeyException e1) {
			return "Error: la clave no es adecuada.";
		} catch (IllegalBlockSizeException e) {
			return "Error por bloque al codificar la frase por "+ e.getMessage();
		} catch (BadPaddingException e) {
			return "Error (por rellenar bloques) al decodificar la frase por "+ e.getMessage();
		}

		/*
		 * Convertimos los bytes decodificados a una cadena de texto y la retornamos.
		 */
		resultado = new String(bytesFrase);
		return resultado;
	}

	
	/*
	 *  Función que realiza la codificación asimetrica de un coche entregado como parametro.
	 */
	private static String codificarCocheASimetrico(Coche pCoche) {

		String resultado;
		SealedObject cocheCodificado = null;

		/*
		 * Iniciamos el codificador en modo codificador con la clave privada
		 * y codificamos el coche codificado
		 */
		try {
			codificador.init(Cipher.ENCRYPT_MODE, clavesAsimetricas.getPrivate());
			cocheCodificado = new SealedObject(pCoche, codificador);
		} catch (InvalidKeyException e) {
			return "Error: la clave no es adecuada.";
		} catch (IllegalBlockSizeException | IOException e) {
			return "Error al decodificar el coche " +e.getMessage();
		}
		
		/*
		 *  Retornamos el coche introducdo y el coche codificado.
		 */
		resultado = "El coche " + pCoche.toString() + "\n" + "Codificado es " + cocheCodificado.toString();
		
		return resultado;
	}

	
	private static String mostrarFraseEncriptada() {
		
		boolean tieneContenido;
		String resultado = "";
		
		/*
		 * comprobramos que tenemos una frase encriptada 
		 */
		tieneContenido = true;
		if (bytesFraseCifrada == null) {
			tieneContenido = false;
			resultado = "No existe una frase que desecriptar";
		} else {
			if (bytesFraseCifrada.length == 0) {
				tieneContenido = false;
				resultado = "No existe una frase que desecriptar";
			}
		}

		/*
		 * Si tiene contenido creamos la frase
		 */
		if (tieneContenido == true) {
			resultado = new String(bytesFraseCifrada);
		}
		
		return resultado;
	}

}
