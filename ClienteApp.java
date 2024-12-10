import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class ClienteApp extends JFrame {
    private ClienteDAO clienteDAO;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    public ClienteApp() {
        clienteDAO = new ClienteDAO();
        setTitle("Gerenciador de Clientes");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Configuração da tabela
        String[] colunas = {"ID", "Nome", "Email", "Telefone"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaClientes = new JTable(modeloTabela);

        // Atualizar os dados na tabela ao iniciar
        atualizarTabela();

        // Painel para adicionar clientes
        JPanel panelAdd = new JPanel(new GridLayout(2, 4, 5, 5));
        JTextField nomeField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField telefoneField = new JTextField();
        JButton addButton = new JButton("Adicionar");
        panelAdd.add(new JLabel("Nome:"));
        panelAdd.add(nomeField);
        panelAdd.add(new JLabel("Email:"));
        panelAdd.add(emailField);
        panelAdd.add(new JLabel("Telefone:"));
        panelAdd.add(telefoneField);
        panelAdd.add(addButton);

        // Painel para remover clientes
        JPanel panelRemove = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(5);
        JButton removeButton = new JButton("Remover");
        panelRemove.add(new JLabel("ID para remover:"));
        panelRemove.add(idField);
        panelRemove.add(removeButton);

        // Painel para busca
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Buscar");
        panelSearch.add(new JLabel("Buscar por Nome:"));
        panelSearch.add(searchField);
        panelSearch.add(searchButton);

        // ScrollPane para a tabela
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);

        // Adicionando os componentes ao layout principal
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panelAdd, BorderLayout.NORTH);
        topPanel.add(panelSearch, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelRemove, BorderLayout.SOUTH);

        // Eventos dos botões
        addButton.addActionListener((ActionEvent e) -> {
            try {
                clienteDAO.adicionarCliente(
                        nomeField.getText(),
                        emailField.getText(),
                        telefoneField.getText()
                );
                atualizarTabela();
                nomeField.setText("");
                emailField.setText("");
                telefoneField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar cliente: " + ex.getMessage());
            }
        });

        removeButton.addActionListener((ActionEvent e) -> {
            try {
                int id = Integer.parseInt(idField.getText());
                clienteDAO.removerCliente(id);
                atualizarTabela();
                idField.setText("");
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao remover cliente: " + ex.getMessage());
            }
        });

        searchButton.addActionListener((ActionEvent e) -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                atualizarTabelaComBusca(searchTerm);
            } else {
                atualizarTabela(); // Mostra todos os dados se a busca estiver vazia
            }
        });
    }

    private void atualizarTabela() {
        try {
            // Limpa os dados existentes
            modeloTabela.setRowCount(0);

            // Adiciona os novos dados
            List<String[]> clientes = clienteDAO.listarClientesComDetalhes();
            for (String[] cliente : clientes) {
                modeloTabela.addRow(cliente);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar a tabela: " + ex.getMessage());
        }
    }

    private void atualizarTabelaComBusca(String nome) {
        try {
            // Limpa os dados existentes
            modeloTabela.setRowCount(0);

            // Adiciona os novos dados correspondentes à busca
            List<String[]> clientes = clienteDAO.buscarClientesPorNome(nome);
            for (String[] cliente : clientes) {
                modeloTabela.addRow(cliente);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar clientes: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteApp app = new ClienteApp();
            app.setVisible(true);
        });
    }
}
