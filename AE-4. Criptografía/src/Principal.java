

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import entidad.Coche;

public class Principal {
	//Iniciamos las variables globales que vamos a usar a lo largo del programa
	static byte[] bytesFraseCifrada;
	static KeyGenerator generadorSimetrico;
	static KeyPairGenerator generadorAsimetrico;
	static SecretKey claveSimetrica;
	static KeyPair clavesAsimetricas;
	static Cipher cifrador = null;
	static Cipher cifradorAsimetrico = null;
	static boolean simetrico = false;
	static boolean asimetrico = false;
	
	public static void main(String[] args) {
		boolean continuar = true;
		Scanner leer = new Scanner(System.in);
		
		//Generamos las claves simetricas y asimetricas ademas del cifrador
		try {
			 generadorSimetrico = KeyGenerator.getInstance("AES");
			 claveSimetrica = generadorSimetrico.generateKey();
			
			 generadorAsimetrico = KeyPairGenerator.getInstance("RSA");
			 clavesAsimetricas = generadorAsimetrico.generateKeyPair();
			 cifrador = Cipher.getInstance("AES");
			 cifradorAsimetrico = Cipher.getInstance("RSA");
		} catch (GeneralSecurityException gse) {
			System.out.println("Algo ha fallado.." + gse.getMessage());
			gse.printStackTrace();
		}
		
		//Creamos un bucle del que no se saldra hasta que el usuario lo inique
		do {
			System.out.println("1) Salir del programa \n2) Encriptar frase \n3) Mostrar frase encriptada \n4) Desencriptar frase \n5) Encriptar Coche ");
			
			//Leemos la opcion del menu que elige el usuario
			String opcion = leer.nextLine();
			
			//Hacemos un switch para realizar las acciones asociadas a al opcion que el usuario eliga.
			switch (opcion) {
				//Se terminara el programa
				case "1":
					continuar = false;
					break;
				
				//Encriptamos una frase eligiendo si hacerlo en simetrico o en asimetrico
				case "2":
					//Pedimos al usuario que introduza la frase que quiere encriptar y la leemos
					System.out.println("Introduce la frase que quieres encriptar:");
					String frase = leer.nextLine();
					
					//Le preguntamos si la quiere cifrar en Simetrico o en Asimetrico
					System.out.println("1)Cifrado Simetrico \n2)Cifrado Asimetrico");
					opcion = leer.nextLine();
					
					//LLamamos a un metodo que la cifrara segun el metodo escogido
					if (opcion.equalsIgnoreCase("1")) {
						codificarSimetrica(frase);
					} else if (opcion.equalsIgnoreCase("2")) {
						codificarAsimetrica(frase);
					}
					break;
				
				//Si existe mostramos la frase encriptada
				case "3":
					//Comprobamos si existe la frase encriptada y si es asi la mostramos convirtiendola en un String
					if (bytesFraseCifrada != null) {
						String fraseCifrada = new String(bytesFraseCifrada);
						System.out.println(fraseCifrada);
					} else {
						System.out.println("Aun nos has cifrado ninguna frase");
					}
					break;
					
				//Si existe desencriptamos la frase
				case "4":
					//Comprobamos si existe la frase encriptada y en funcion de si se ha cifrado en simetrico o en asimetrico llamaremos a un metodo que la desencriptara
					if (bytesFraseCifrada != null) {
						if (simetrico == true) {
							descodificarSimetrica();
						} else if (asimetrico == true) {
							descodificarAsimetrica();
						}
					}else {
						System.out.println("Aun nos has cifrado ninguna frase");
					}
					break;
					
				//Encriptamos un objeto coche
				case "5":
					//Creamos un objeto coche y pedimos al usuario que nos introduzca sus atributos
					Coche coche = new Coche();
					System.out.println("Cual es la matricula del coche: ");
					opcion = leer.nextLine();
					coche.setMatricula(opcion);
					System.out.println("Cual es la marca del coche: ");
					opcion = leer.nextLine();
					coche.setMarca(opcion);
					System.out.println("Cual es el modelo del coche: ");
					opcion = leer.nextLine();
					coche.setModelo(opcion);
					System.out.println("Cual es el precio del coche: ");
					opcion = leer.nextLine();
					
					//Si el coche se ha creado bien preguntamos si lo quiere encriptar en simetrico o en asimetrico y llamamos al meetodo correspondiente
					try {
						coche.setPrecio(Integer.parseInt(opcion));
						System.out.println("1)Cifrado simetrico \n2)Cifrado asimetrico");
						opcion = leer.nextLine();
						if (opcion.equalsIgnoreCase("1")) {
							cifrarCocheSimetrico(coche);
						} else if (opcion.equalsIgnoreCase("2")) {
							cifrarCocheAsimetrico(coche);
						}
					} catch (NumberFormatException e) {
						System.out.println("La matricula tiene que ser un numero");
					}
					break;
					
				//Por defecto pedira que introduzca un numero del menu
				default:
					System.out.println("Porfavor introduzca el numero de la opcion que desea:");
					break;
			}
		} while (continuar);

	}

	//Metodo que codificara la frase en simetrico
	private static void codificarSimetrica(String frase) {
		try {
			//Convertimos la frase a bytes
			byte[] bytesFraseOriginal = frase.getBytes();
			
			//Configuramos el cifrador en asimetrido y ciframos la frase con la clave simetrica
			cifrador.init(Cipher.ENCRYPT_MODE, claveSimetrica);
			bytesFraseCifrada = cifrador.doFinal(bytesFraseOriginal);
			
			//Convertimos el booleano simetrico en true para que desencripte automaticamente con la clave simetrico
			simetrico = true;
			asimetrico = false;
		} catch (GeneralSecurityException gse) {
			System.out.println("Algo ha fallado.." + gse.getMessage());
			gse.printStackTrace();
		}
	}
	
	
	//Metodo que codificara la frase en asimetrico
	private static void codificarAsimetrica(String frase) {
		try {
			//Convertimos la frase a bytes
			byte[] bytesFraseOriginal = frase.getBytes();
			
			//Configuramos el cifrador en asimetrido y ciframos la frase con la clave publica para asegurarnos que solo la podemos descifrar nosotros 
			cifradorAsimetrico.init(Cipher.ENCRYPT_MODE, clavesAsimetricas.getPublic());
			bytesFraseCifrada = cifradorAsimetrico.doFinal(bytesFraseOriginal);
			
			//Convertimos el booleano asimetrico en true para que desencripte automaticamente con la clave simetrico privada
			simetrico = false;
			asimetrico = true;
		} catch (GeneralSecurityException gse) {
			System.out.println("Algo ha fallado.." + gse.getMessage());
			gse.printStackTrace();
		}
	}
	
	
	//Metodo que descodificara la frase en simetrico
	private static void descodificarSimetrica() {
		try {
			//Configuramos el codificador en simetrico y desencriptamos la frase con la clave simetrica
			cifrador.init(Cipher.DECRYPT_MODE, claveSimetrica);
			byte[] bytesFraseDescifrada = cifrador.doFinal(bytesFraseCifrada);
			
			//Convertimos en String la fase desencriptada y la imprimimos por pantalla
			String fraseDescifrada = new String(bytesFraseDescifrada);
			System.out.println(fraseDescifrada);
		} catch (GeneralSecurityException gse) {
			System.out.println("Algo ha fallado.." + gse.getMessage());
			gse.printStackTrace();
		}
	}

	
	//Metodo que descodificara la frase en asimetrico
	private static void descodificarAsimetrica() {
		try {
			//Configuramos el codificador en asimetrico y desencriptamos la frase con la clave privada
			cifradorAsimetrico.init(Cipher.DECRYPT_MODE, clavesAsimetricas.getPrivate());
			byte[] bytesFraseDescifrada = cifradorAsimetrico.doFinal(bytesFraseCifrada);
			
			//Convertimos en String la fase desencriptada y la imprimimos por pantalla
			String fraseDescifrada = new String(bytesFraseDescifrada);
			System.out.println(fraseDescifrada);
		} catch (GeneralSecurityException gse) {
			System.out.println("Algo ha fallado.." + gse.getMessage());
			gse.printStackTrace();
		}
	}
	
	//Metodo que codificara el coche en simetrico
	private static void cifrarCocheSimetrico(Coche coche) {
		try {
			//Configuramos el codificador en simetrico con la clave simetrica y usamos el objeto SealedObject para encriptarlo
			cifrador.init(Cipher.ENCRYPT_MODE, claveSimetrica);
			SealedObject so = new SealedObject(coche, cifrador);
			System.out.println("El coche se ha cifrado: \n" + so.toString());
		} catch (Exception gse) {
			System.out.println("Algo ha fallado.." + gse.getMessage());
			gse.printStackTrace();
		}
	}
	
	//Metodo que codificara el coche en asimetrico
	private static void cifrarCocheAsimetrico(Coche coche) {
		try {
			//Configuramos el codificador en asimetrico y usamos el objeto SealedObject para encriptarlo con la clave publica y asi asegurarnos que solo la podemos desencriptar nosotros
			cifradorAsimetrico.init(Cipher.ENCRYPT_MODE, clavesAsimetricas.getPublic());
			SealedObject so = new SealedObject(coche, cifradorAsimetrico);
			System.out.println("El coche se ha cifrado: \n" + so.toString());
		} catch (Exception gse) {
			System.out.println("Algo ha fallado.." + gse.getMessage());
			gse.printStackTrace();
		}
	}
}
