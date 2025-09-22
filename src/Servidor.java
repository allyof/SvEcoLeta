import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORT = 5005; // Porta do servidor
    private static final String SERVERID = "src" + File.separator + "arquivos"; // Local onde os arquivos serão salvos


    public static void main(String[] args) throws Exception {
        // Cria o local de  arquivos, se ele não existir
        File dir = new File(SERVERID);
        if (!dir.exists()) dir.mkdirs();

        // Conecta no servidor
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Conectado");

        // Loop infinito para aceitar múltiplas conexões de clientes
        while (true) {
            Socket socket = serverSocket.accept(); // Aceita conexão de cliente
            System.out.println("Cliente conectou: " + socket.getInetAddress());

            // Cria uma nova thread para lidar com cada cliente
            new Thread(() -> {
                try {
                    handleClient(socket); // Processa requisições do cliente
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // Método que processa os comandos enviados pelo cliente
    private static void handleClient(Socket socket) throws IOException {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Leitura de comandos
            PrintStream saida = new PrintStream(socket.getOutputStream()); // Escrita de respostas
        ) {
            String comando;
            while ((comando = reader.readLine()) != null) {
                // LIST: envia a lista de arquivos disponíveis
                if (comando.equals("LIST")) {
                    File[] files = new File(SERVERID).listFiles();
                    for (File file : files) {
                        saida.println(file.getName());
                    }
                    saida.println("EOF"); // Marca o fim da lsita

                // UPLOAD <nome>: envia os arquivos do cliente para o servidor
                } else if (comando.startsWith("UPLOAD")) {
                    String filename = comando.split(" ")[1];
                    FileOutputStream fos = new FileOutputStream(SERVERID + File.separator + filename);
                    int tamRead;
                    while ((tamRead = socket.getInputStream().read()) != -1) {
                        if (tamRead == 0) break; // fim do arquivo personalizado
                        fos.write(tamRead);
                    }
                    fos.close();
                    saida.println("Arquivo recebido com sucesso");

                // DOWNLOAD <nome>: cliente baixa o arquivo ja armazenado
                } else if (comando.startsWith("DOWNLOAD")) {
                    String filename = comando.split(" ")[1];
                    File file = new File(SERVERID + File.separator + filename);
                    if (file.exists()) {
                        FileInputStream fis = new FileInputStream(file);
                        int tamRead;
                        while ((tamRead = fis.read()) != -1) {
                            socket.getOutputStream().write(tamRead);
                        }
                        socket.getOutputStream().write(0); // fim do arquivo personalizado
                        fis.close();
                    } else {
                        saida.println("Arquivo não encontrado.");
                    }

                // Comando inválido
                } else {
                    saida.println("Comando inválido.");
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao lidar com cliente: " + e.getMessage());
        }
    }
}


