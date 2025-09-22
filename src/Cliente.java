import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {
    private static final String SERVER_IP = "localhost"; // IP do servidor
    private static final int PORT = 5005; // Porta do servidor

    public static void main(String[] args) throws UnknownHostException, IOException {
        // Conecta ao servidor
        Socket socket = new Socket(SERVER_IP, PORT);
        PrintStream saida = new PrintStream(socket.getOutputStream()); // Envia comandos
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Recebe respostas
        Scanner scanner = new Scanner(System.in); // Entrada do usuário

        // Menu interativo
        while (true) {
            System.out.println("\n1. Enviar arquivo\n2. Listar arquivos\n3. Baixar arquivo\n4. Sair");
            String opcao = scanner.nextLine();

            // Envio de arquivo
            if (opcao.equals("1")) {
                System.out.print("Nome do arquivo local: ");
                String filename = scanner.nextLine();
                File file = new File(filename);
                if (!file.exists()) {
                    System.out.println("Arquivo não encontrado.");
                    continue;
                }

                // Envia comando e conteúdo do arquivo
                saida.println("UPLOAD " + file.getName());
                FileInputStream fis = new FileInputStream(file);
                int tamRead;
                while ((tamRead = fis.read()) != -1) {
                    socket.getOutputStream().write(tamRead);
                }
                socket.getOutputStream().write(0); // EOF personalizado
                fis.close();
                System.out.println(entrada.readLine()); // Confirmação do servidor

            // Listagem de arquivos
            } else if (opcao.equals("2")) {
                saida.println("LIST");
                String line;
                while (!(line = entrada.readLine()).equals("EOF")) {
                    System.out.println("- " + line);
                }

            // Download de arquivo
            } else if (opcao.equals("3")) {
                System.out.print("Nome do arquivo para baixar: ");
                String filename = scanner.nextLine();
                saida.println("DOWNLOAD " + filename);
                FileOutputStream fos = new FileOutputStream("download_" + filename);
                int tamRead;
                while ((tamRead = socket.getInputStream().read()) != 0) {
                    fos.write(tamRead);
                }
                fos.close();
                System.out.println("Arquivo baixado com sucesso.");

            // Encerrar conexão
            } else if (opcao.equals("4")) {
                socket.close();
                break;
            }
        }
    }
}
