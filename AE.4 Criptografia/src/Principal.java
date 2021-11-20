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

	static String fraseEncriptadaSimetrica;
	static String fraseEncriptadaAsimetrica;
	static SealedObject cocheCodificado;
	
	static KeyGenerator generadorSimetrico;
	static KeyPairGenerator generadorAsimetrico;

	static SecretKey claveSimetrica;
	static KeyPair clavesAsimetricas;
	
	
	
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
	private static  void CriptografiaSimetrica() {
		boolean continuar = true;
		String opcion;
		
		String frase = "";
		cocheCodificado = null;
		Coche cocheIntroducido;

		while (continuar == true) {
			
			mostrarMenu();		
			opcion = lector.nextLine();
			switch (opcion) {
			case "1"://Con la opción 1 el programa le pedirá al usuario una frase, la encriptará y la guardará en memoria.
				System.out.println("Introduce la frase a encriptar");		
				frase = lector.nextLine();		
				codificarSimetrica(frase);
				frase = "";
				break;
			case "2"://Con la opción 2 el programa mostrará la frase encriptada (no debería ser legible)
				System.out.println(fraseEncriptadaSimetrica);
				break;				
			case "3"://Con la opción 3 el programa mostrará la frase desencriptándola.
				System.out.println(decodificarSimetrica());
				break;
			case "4"://Encriptar Coche
				cocheIntroducido = IntroducirCoche();
				codificarCocheSimetrico(cocheIntroducido);
				break;				
			case "5":
				continuar = false;
				break;
			default:
			}
		}

	}

	private static void CriptografiaAsimetrica() {
		boolean continuar = true;
		String opcion;

		String frase = "";
		cocheCodificado = null;
		
		Coche cocheIntroducido;
		
		while (continuar == true) {
			mostrarMenu();		
			opcion = lector.nextLine();
			switch (opcion) {
			case "1"://Con la opción 1 el programa le pedirá al usuario una frase, la encriptará y la guardará en memoria.
				System.out.println("Introduce la frase a encriptar");		
				frase = lector.nextLine();		
				codificarAsimetrica(frase);
				frase = "";
				break;
			case "2"://Con la opción 2 el programa mostrará la frase encriptada (no debería ser legible)
				System.out.println(fraseEncriptadaAsimetrica);
				break;				
			case "3"://Con la opción 3 el programa mostrará la frase desencriptándola.
				System.out.println(decodificarAsimetrica());
				break;
			case "4"://Encriptar Coche
				cocheIntroducido = IntroducirCoche();
				codificarCocheASimetrico(cocheIntroducido);
				break;				
			case "5":
				continuar = false;
				break;
			default:
			}
		}

	}
	
	private static void mostrarMenu() {
		System.out.println("1- Encriptar frase");
		System.out.println("2- Mostrar frase encriptada");
		System.out.println("3- Desencriptar frase");
		System.out.println("4- Encriptar Coche");
		System.out.println("5- Volver al menu principal");		
	}

	private static Coche IntroducirCoche() {
		Coche resultado = new Coche();
		
		String matricula;		
		String marca;		
		String modelo;		
		String precio;
		

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

		resultado = new Coche(matricula, marca, modelo, precio);
		
		return resultado;
	}
	
	
	private static void codificarSimetrica(String frase) {

		byte[] bytesFrase;
		byte[] bytesMensajeCifrado;

		Cipher codificador = null;
				
		try {
			codificador = Cipher.getInstance("AES");
			codificador.init(Cipher.ENCRYPT_MODE, claveSimetrica);
			bytesFrase = frase.getBytes();
			bytesMensajeCifrado = codificador.doFinal(bytesFrase);//codificar el mensaje original
			fraseEncriptadaSimetrica = new String(bytesMensajeCifrado);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error al crear la instancia codificadora");
			return;
		} catch (InvalidKeyException e) {
			System.out.println("Error: la clave no es adecuada.");
			return;
		} catch (IllegalBlockSizeException e) {
			System.out.println("Error por bloque al codificar la frase por "+ e.getMessage());
			return;
		} catch (BadPaddingException e) {
			System.out.println("Error (por rellenar bloques) al codificar la frase por "+ e.getMessage());
			return;
		}
		
		
	}

	private static String decodificarSimetrica() {
		
		String resultado = "";
		
		byte[] bytesFrase;
		byte[] bytesFraseCifrada;

		Cipher descodificador = null;
		
		try {
			descodificador = Cipher.getInstance("AES");
			descodificador.init(Cipher.DECRYPT_MODE, claveSimetrica);
			bytesFraseCifrada = fraseEncriptadaSimetrica.getBytes();
			bytesFrase = descodificador.doFinal(bytesFraseCifrada);//decodificar el mensaje codificado
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error al crear la instancia decodificadora");
			return "";
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

		resultado = new String(bytesFrase);
		
		return resultado;
	}
	
	private static void codificarCocheSimetrico(Coche pCoche) {

		Cipher codificador = null;
				
		try {
			codificador = Cipher.getInstance("AES");
			codificador.init(Cipher.ENCRYPT_MODE, claveSimetrica);
			cocheCodificado = new SealedObject(pCoche, codificador);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error al crear la instancia decodificadora");
			return;
		} catch (InvalidKeyException e) {
			System.out.println("Error: la clave no es adecuada.");
			return;
		} catch (IllegalBlockSizeException | IOException e) {
			System.out.println("Error al decodificar el coche" + e.getMessage());
			return;
		}
		
		System.out.println("El coche " + pCoche.toString());
		System.out.println("Codificado es " + cocheCodificado.toString());
		
		
	}
	
	private static void codificarAsimetrica(String frase) {
			
			Cipher cifrador = null;

			byte[] bytesFrase;
			byte[] bytesFraseCifrada;

			
			
			try {
				cifrador = Cipher.getInstance("RSA");
				cifrador.init(Cipher.ENCRYPT_MODE, clavesAsimetricas.getPrivate());
				bytesFrase = frase.getBytes();
				bytesFraseCifrada = cifrador.doFinal(bytesFrase);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				System.out.println("Error al crear la instancia codificadora");
				return;
			} catch (InvalidKeyException e) {
				System.out.println("Error: la clave no es adecuada.");
				return;
			} catch (IllegalBlockSizeException e) {
				System.out.println("Error por bloque al codificar la frase por "+ e.getMessage());
				return;
			} catch (BadPaddingException e) {
				System.out.println("Error (por rellenar bloques) al codificar la frase por "+ e.getMessage());
				return;
			}
			
			fraseEncriptadaAsimetrica = new String(bytesFraseCifrada);
			
	}

	private static String decodificarAsimetrica() {
		String resultado = "";
		
		Cipher descifrador = null;

		byte[] bytesFrase;
		byte[] bytesFraseCifrada;

				
		try {
			descifrador = Cipher.getInstance("RSA");
			bytesFraseCifrada = fraseEncriptadaAsimetrica.getBytes();
			descifrador.init(Cipher.DECRYPT_MODE, clavesAsimetricas.getPublic());
			bytesFrase = descifrador.doFinal(bytesFraseCifrada);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e2) {
			System.out.println("Error al crear la instancia decodificadora");
			return "";
		} catch (InvalidKeyException e1) {
			System.out.println("Error: la clave no es adecuada.");
			return "";
		} catch (IllegalBlockSizeException e) {
			System.out.println("Error por bloque al codificar la frase por "+ e.getMessage());
			return "";
		} catch (BadPaddingException e) {
			System.out.println("Error (por rellenar bloques) al codificar la frase por "+ e.getMessage());
			return "";
		}
		
		resultado = new String(bytesFrase);
		return resultado;
	}

	private static void codificarCocheASimetrico(Coche pCoche) {

		Cipher codificador = null;
				
		try {
			codificador = Cipher.getInstance("RSA");
			codificador.init(Cipher.ENCRYPT_MODE, clavesAsimetricas.getPrivate());
			cocheCodificado = new SealedObject(pCoche, codificador);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error al crear la instancia decodificadora");
			return;
		} catch (InvalidKeyException e) {
			System.out.println("Error: la clave no es adecuada.");
			return;
		} catch (IllegalBlockSizeException | IOException e) {
			System.out.println("Error al decodificar el coche " +e.getMessage() );
			return;
		}
		
		System.out.println("El coche " + pCoche.toString());
		System.out.println("Codificado es " +cocheCodificado.toString());
		
	}
	

}
