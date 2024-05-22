package program;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private int quantidade;

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
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

    public void salvar() {
        String sql = "INSERT INTO Produto (nome, preco, quantidade) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, getNome());
            stmt.setDouble(2, getPreco());
            stmt.setInt(3, getQuantidade());
            stmt.executeUpdate();

            // Obter o ID gerado
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
