import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORT = 5005; // Porta do servidor
    private static final String PASTASERVIDOR = "src" + File.separator + "arquivos"; // Local onde os arquivos serão salvos


    public static void main(String[] args) throws Exception {
        // Cria o local de  arquivos, se ele não existir
        File file = new File(PASTASERVIDOR);
        file.getParentFile().mkdirs(); //Garante que a pasta eixsta
        file.createNewFile(); // Cria o arquivo se ele não existir

        // Conecta no servidor
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Conectado ao EcoLeta");

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
                //Adicionando pontos de coleta
                if (comando.contains("ADICIONAR")) {
                    try (FileWriter fwq = new FileWriter(PASTASERVIDOR, true)) {
                        fwq.write(comando.substring(4) + "\n");
                    }
                    saida.println("Ponto de coleta adicionado com sucesso.");
                    
                } else if(comando.contains("PESQUISA")) {
                    // Busca por nome
                    String nomeBusca = comando.substring(7).trim();
                    boolean encontrado = false;
                    try (BufferedReader fileReader = new BufferedReader(new FileReader(PASTASERVIDOR))){
                        String linha;
                        while ((linha = fileReader.readLine()) != null) {
                            if (linha.contains(nomeBusca)) {
                                saida.println(linha);
                                encontrado = true;
                                break;
                            }
                        }

                    } if (!encontrado) saida.println("Ponto não cadastrado ou não existente.");
                
            } else if(comando.equals("LISTA")){
                //Faz uma lista com todos os pontos cadastrados
                try (BufferedReader fileReader = new BufferedReader(new FileReader(PASTASERVIDOR))) {
                    String linha;
                    while ((linha = fileReader.readLine()) != null) {
                        saida.println(linha);
                    }
                } saida.println("Fim do arquivo.");
            } else {
                    saida.println("Comando inválido.");
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao lidar com cliente: " + e.getMessage());
        }
    }
}


