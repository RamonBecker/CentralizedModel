package br.ifsc.edu.canoinhas.ads.cs.servidor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ServidorWorker implements Runnable {

	private Socket s;
	private String requisicaoCliente;
	private String respostaPOST;
	private String[] separarPOST;
	private String email = "admin@admin.com";
	private String senha = "admin";
	private String[] emailSeparadoPOST;
	private String[] senhaSeparadoPOST;

	public ServidorWorker(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {

		try {

			String protocolo = "";

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));

			requisicaoCliente = inFromClient.readLine();

			while (!requisicaoCliente.isEmpty()) {

				System.out.println(requisicaoCliente);

				requisicaoCliente = inFromClient.readLine();
			}

			StringBuilder load = new StringBuilder();

			while (inFromClient.ready()) {
				load.append((char) inFromClient.read());
			}

			respostaPOST = load.toString();

			separarPOST = respostaPOST.split("&");

			System.out.println(respostaPOST);

			if (separarPOST.length > 1) {

				emailSeparadoPOST = separarPOST[0].split("=");
				senhaSeparadoPOST = separarPOST[1].split("=");
				emailSeparadoPOST[1] = emailSeparadoPOST[1].replaceAll("%40", "@");

				if (emailSeparadoPOST[1].replaceAll("%", "@").contentEquals(email)
						& senhaSeparadoPOST[1].contentEquals(senha)) {
					protocolo = "200";
				} else {
					protocolo = "401";
				}

			}

			DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());

			outToClient.write(montarResponse(protocolo).getBytes());
			outToClient.flush();
			outToClient.close();

			// System.out.println(montarheader(protocolo));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String montarResponse(String protocolo) {

		String response = "";
		SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
		formatador.setTimeZone(TimeZone.getTimeZone("GMT"));

		if (protocolo.contentEquals("200")) {
			response = "HTTP/1.1 " + protocolo + "OK" + "\n\n" + "<h1>Acesso autorizado<h1>";
		}

		if (protocolo.contentEquals("401")) {
			response = "HTTP/1.1 " + protocolo + "\n\n" + "<h1>Acesso não autorizado<h1>";
		}

		return response;

	}

}
