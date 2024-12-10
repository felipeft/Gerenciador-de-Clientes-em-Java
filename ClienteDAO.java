import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection connection;

    public ClienteDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void adicionarCliente(String nome, String email, String telefone) throws SQLException {
        // Verificar se a tabela está vazia
        if (isTabelaVazia()) {
            resetarSequencia();
        }

        // Inserir cliente
        String sql = "INSERT INTO clientes (nome, email, telefone) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, telefone);
            stmt.executeUpdate();
        }
    }

    private boolean isTabelaVazia() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM clientes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        }
        return false;
    }

    private void resetarSequencia() throws SQLException {
        String sql = "ALTER SEQUENCE clientes_id_seq RESTART WITH 1";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public void removerCliente(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<String> listarClientes() throws SQLException {
        String sql = "SELECT * FROM clientes";
        List<String> clientes = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(rs.getInt("id") + " - " + rs.getString("nome"));
            }
        }
        return clientes;
    }

    public List<String[]> listarClientesComDetalhes() throws SQLException {
        String sql = "SELECT id, nome, email, telefone FROM clientes";
        List<String[]> clientes = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] cliente = {
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("telefone")
                };
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    public List<String[]> buscarClientesPorNome(String nome) throws SQLException {
        String sql = "SELECT id, nome, email, telefone FROM clientes WHERE nome ILIKE ?";
        List<String[]> clientes = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] cliente = {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("telefone")
                    };
                    clientes.add(cliente);
                }
            }
        }
        return clientes;
    }
    
    
}
