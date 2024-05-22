package program;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CafeCommerce {
    private int quantidadeVendida;
    private double precoUnitario;
    private double totalVenda;
    private double despesaTotal;
    private List<Produto> listaProdutos;

    public CafeCommerce() {
        listaProdutos = new ArrayList<>();
    }

    public void processarVenda() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Digite a quantidade vendida: ");
            quantidadeVendida = scanner.nextInt();

            System.out.print("Digite o preço unitário: ");
            precoUnitario = scanner.nextDouble();

            totalVenda = quantidadeVendida * precoUnitario;

            System.out.println("Venda processada com sucesso!");
            System.out.println("Quantidade vendida: " + quantidadeVendida);
            System.out.println("Preço Unitário: " + precoUnitario);
            System.out.println("Total da Venda: " + totalVenda);

            System.out.println("Digite 'seguinte' para registrar os produtos adquiridos para a cafeteria.");

            String input = scanner.next();
            if (input.equalsIgnoreCase("seguinte")) {
                processarProdutos(scanner);
            }

            salvarVenda();
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao processar a venda. Certifique-se de inserir valores válidos.");
        } finally {
            scanner.close();
        }
    }

    private void processarProdutos(Scanner scanner) {
        boolean adicaoProdutosBemSucedida = adicionarProdutos(scanner);

        if (adicaoProdutosBemSucedida) {
            exibirRelatorioFinal();
        }
    }

    private boolean adicionarProdutos(Scanner scanner) {
        boolean adicaoBemSucedida = true;

        while (true) {
            Produto produto = adicionarProduto(scanner);
            if (produto != null) {
                listaProdutos.add(produto);
                despesaTotal += produto.getPreco() * produto.getQuantidade();
                produto.salvar(); // Salva o produto no banco de dados
            } else {
                adicaoBemSucedida = false;
            }

            System.out.println();
            System.out.println("Despesa total com produtos: R$" + despesaTotal);
            System.out.println();

            if (!adicaoBemSucedida) {
                return false;
            }

            System.out.println("Deseja adicionar mais produtos? (sim/não)");
            String continuar = scanner.next();
            if (!continuar.equalsIgnoreCase("sim")) {
                break;
            }
        }

        return true;
    }

    private Produto adicionarProduto(Scanner scanner) {
        Produto produto = new Produto();

        while (true) {
            try {
                scanner.nextLine(); // Consumir a nova linha
                System.out.print("Digite o nome do produto: ");
                String nomeProduto = scanner.nextLine();
                produto.setNome(nomeProduto);

                System.out.print("Digite o preço do produto: ");
                double preco = Double.parseDouble(scanner.nextLine());

                System.out.print("Digite a quantidade do produto: ");
                int quantidade = Integer.parseInt(scanner.nextLine());

                produto.setPreco(preco);
                produto.setQuantidade(quantidade);

                System.out.println("Produto adicionado com sucesso:");
                produto.exibirInformacoes();
                System.out.println();

                return produto;

            } catch (NumberFormatException e) {
                System.out.println("Ocorreu um erro ao adicionar o produto. Certifique-se de inserir valores válidos.");
                System.out.println("Deseja tentar adicionar o produto novamente? (sim/não)");
                String tentarNovamente = scanner.nextLine();
                if (!tentarNovamente.equalsIgnoreCase("sim")) {
                    break;
                }
            }
        }

        return null;
    }

    private void salvarVenda() {
        String sqlVenda = "INSERT INTO Venda (quantidadeVendida, precoUnitario, totalVenda, despesaTotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmtVenda.setInt(1, quantidadeVendida);
            stmtVenda.setDouble(2, precoUnitario);
            stmtVenda.setDouble(3, totalVenda);
            stmtVenda.setDouble(4, despesaTotal);
            stmtVenda.executeUpdate();

            // Obter o ID da venda recém-inserida
            ResultSet generatedKeys = stmtVenda.getGeneratedKeys();
            if (generatedKeys.next()) {
                long vendaId = generatedKeys.getLong(1);
                salvarProdutosDaVenda(vendaId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void salvarProdutosDaVenda(long vendaId) {
        String sqlVendaProduto = "INSERT INTO VendaProduto (venda_id, produto_id, quantidade, preco, total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtVendaProduto = conn.prepareStatement(sqlVendaProduto)) {
            
            for (Produto produto : listaProdutos) {
                stmtVendaProduto.setLong(1, vendaId);
                stmtVendaProduto.setLong(2, produto.getId()); // Supondo que Produto tenha um método getId()
                stmtVendaProduto.setInt(3, produto.getQuantidade());
                stmtVendaProduto.setDouble(4, produto.getPreco());
                stmtVendaProduto.setDouble(5, produto.calcularTotal());
                stmtVendaProduto.addBatch();
            }
            stmtVendaProduto.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exibirRelatorioFinal() {
        double totalVendasProdutos = 0;
        for (Produto produto : listaProdutos) {
            totalVendasProdutos += produto.calcularTotal();
        }
        double lucro = totalVenda - despesaTotal;
        System.out.println("Total de Vendas de Produtos: R$" + totalVendasProdutos);
        System.out.println("Total de Vendas de Café: R$" + totalVenda);
        System.out.println("Despesa Total com Produtos: R$" + despesaTotal);
        System.out.println("Lucro: R$" + lucro);
    }

    public static void main(String[] args) {
        CafeCommerce cafe = new CafeCommerce();
        cafe.processarVenda();
    }
}
