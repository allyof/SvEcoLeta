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
            System.out.println("\n=== Menu EcoLeta ===");;
            System.out.println("\n1. Consultar ponto de coleta por nome");
            System.out.println("2. Listar todos os pontos de coleta");
            System.out.println("\n---------------------");
            System.out.println("\nAdministração: ");
            System.out.println("3. Adicionar ponto de coleta");
            System.out.println("\n---------------------");        
            System.out.println("4. Sair");
            String opcao = scanner.nextLine();

            if (opcao.equals("3")) { //Acesso de administração
                System.out.print("Digite a senha de administrador: ");
                String senha = scanner.nextLine();
                    if (!senha.equals("admin1234")) {
                        System.out.println("Senha incorreta. Acesso negado.");
                        continue;
                    }
                System.out.print("Nome do posto de coleta (preferencia minusculo): "); //Adicionar pontos de coleta
                String nome = scanner.nextLine();
                System.out.print("Endereço do posto de coleta: ");
                String endereco = scanner.nextLine();
                System.out.print("Materiais aceitos (separar por virgula): ");
                String materiais = scanner.nextLine();
                saida.println("ADICIONAR " + nome + ";" + endereco + ";" + materiais);
                System.out.println(entrada.readLine());

            } else if (opcao.equals("1")) { //Consulta por nome
                System.out.print("Nome do ponto de coleta(necessario que o ponto esteja escrito corretamente): ");
                String nome = scanner.nextLine();
                saida.println("PESQUISAR " + nome);
                String resposta = entrada.readLine();
                System.err.println("\nResultado da busca:\n");
                if (resposta.startsWith(nome + ";")) {
                    String [] dados = resposta.split(";");
                    System.out.println("Nome: " + dados[0]);
                    System.out.println("Endereço: " + dados[1]);
                    System.out.println("Materiais aceitos: " + dados[2]);
                    System.out.println("------------------------" );
                } else {
                    System.out.println("Ponto de coleta não encontrado.");
                }   

            } else if (opcao.equals("2")) { //Listar todos os pontos
                saida.println("LISTAR");
                System.out.println("\nLista de pontos de coleta:\n");

                String linha;
                int cont = 1;
                while (!(linha = entrada.readLine()).equals("EOF")) {
                    String[] dados = linha.split(";");
                    System.out.println("Ponto de coleta #" + cont++);
                    System.out.println("Nome: " + dados[0]);
                    System.out.println("Endereço: " + dados[1]);
                    System.out.println("Materiais aceitos: " + dados[2]);
                    System.out.println("------------------------" );
                }

            } else if (opcao.equals("4")) { //finalizar
                socket.close();
                break;
            }
        }
    }
}
