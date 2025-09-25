import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORT = 5005; // Porta do servidor
    private static final String PASTASERVIDOR = "src" + File.separator + "dados"; // Local onde os dados serão salvos


    public static void main(String[] args) throws Exception {
        // Cria o local de  dados, se ele não existir
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
                if (comando.startsWith("ADICIONAR")) {
                    try (FileWriter fwq = new FileWriter(PASTASERVIDOR, true)) {
                        fwq.write(comando.substring(10) + "\n");
                    }
                    saida.println("Ponto de coleta adicionado com sucesso.");
                    
                } else if (comando.startsWith("PESQUISAR")) { //Busca por nome
                String nomeBusca = comando.substring(10).trim();
                boolean encontrado = false;
                try (BufferedReader fileReader = new BufferedReader(new FileReader(PASTASERVIDOR))) { //Lê o arquivo de dados
                String linha;
                while ((linha = fileReader.readLine()) != null) {
                if (linha.startsWith(nomeBusca + ";")) { //Verifica se o nome do ponto bate com o nome buscado
                saida.println(linha);
                encontrado = true;
            }
        }
    }
    if (!encontrado) saida.println("Ponto não cadastrado ou não existente."); //Mensagem de erro caso o ponto não seja encontrado
                
            } else if(comando.equals("LISTAR")){
                //Faz uma lista com todos os pontos cadastrados
                try (BufferedReader fileReader = new BufferedReader(new FileReader(PASTASERVIDOR))) { //Lê o arquivo de dados
                    String linha;
                    while ((linha = fileReader.readLine()) != null) {
                        saida.println(linha);
                    }
                } saida.println("EOF"); //Marca o fim da lista
            } else {
                    saida.println("Comando inválido."); //Mensagem de erro para comando inválido
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao lidar com cliente: " + e.getMessage()); //Mensagem de erro
        }
    }
}


