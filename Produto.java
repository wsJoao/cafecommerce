package program;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Produto {
    private String nome;
    private double preco;
    private int quantidade;

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double calcularTotal() {
        return preco * quantidade;
    }

    public void exibirInformacoes() {
        System.out.println("Nome do produto: " + nome);
        System.out.println("Preço unitário: R$" + preco);
        System.out.println("Quantidade: " + quantidade);
        System.out.println("Preço total: R$" + calcularTotal());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Produto> listaProdutos = new ArrayList<>();
        double totalCompra = 0;

        // Loop para adicionar vários produtos
        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Adicionar produto");
            System.out.println("2 - Editar produto");
            System.out.println("3 - Sair");

            int opcao = 0;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Por favor, digite um número.");
                continue;
            }

            switch (opcao) {
                case 1:
                    Produto novoProduto = adicionarProduto(scanner);
                    listaProdutos.add(novoProduto);
                    totalCompra += novoProduto.calcularTotal();
                    break;
                case 2:
                    editarProduto(listaProdutos, scanner);
                    totalCompra = calcularTotalCompra(listaProdutos);
                    break;
                case 3:
                    System.out.println("Programa encerrado.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida.");
            }

            System.out.println();
            System.out.println("Total da Compra: R$" + totalCompra);
            System.out.println();
        }
    }

    public static Produto adicionarProduto(Scanner scanner) {
        Produto produto = new Produto();

        // Solicitar nome do produto
        System.out.print("Digite o nome do produto: ");
        produto.setNome(scanner.nextLine());

        // Solicitar preço do produto
        double precoProduto = 0;
        try {
            System.out.print("Digite o preço do produto: ");
            precoProduto = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Preço inválido. Por favor, digite um número válido.");
            return null;
        }
        produto.setPreco(precoProduto);

        // Solicitar quantidade do produto
        int quantidadeProduto = 0;
        try {
            System.out.print("Digite a quantidade do produto: ");
            quantidadeProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Quantidade inválida. Por favor, digite um número válido.");
            return null;
        }
        produto.setQuantidade(quantidadeProduto);

        // Exibir informações do produto adicionado
        System.out.println("Produto adicionado com sucesso:");
        produto.exibirInformacoes();
        System.out.println();

        return produto;
    }

    public static void editarProduto(List<Produto> listaProdutos, Scanner scanner) {
        System.out.println("Lista de Produtos:");
        for (int i = 0; i < listaProdutos.size(); i++) {
            System.out.println((i + 1) + " - " + listaProdutos.get(i).getNome());
        }

        System.out.print("Escolha o número do produto que deseja editar: ");
        int numeroProduto = 0;
        try {
            numeroProduto = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Número de produto inválido. Por favor, digite um número.");
            return;
        }

        if (numeroProduto >= 1 && numeroProduto <= listaProdutos.size()) {
            Produto produto = listaProdutos.get(numeroProduto - 1);

            System.out.println("Editando produto: " + produto.getNome());
            Produto novoProduto = adicionarProduto(scanner);
            if (novoProduto != null) {
                listaProdutos.set(numeroProduto - 1, novoProduto);
            }
        } else {
            System.out.println("Número de produto inválido.");
        }
    }

    public static double calcularTotalCompra(List<Produto> listaProdutos) {
        double totalCompra = 0;
        for (Produto produto : listaProdutos) {
            totalCompra += produto.calcularTotal();
        }
        return totalCompra;
    }
}