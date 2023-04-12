package br.com.techos.telas;
import java.sql.*;
import br.com.techos.dal.ModuloConexao;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;




public class TelaOS extends javax.swing.JInternalFrame {
    //usando a variavel conexao do DAL
    Connection conexao = null;
    //criando variaveis especiais para a conexao com o banco
    //prepared statement e resultSet são frameworks do pacote java.sql e servem para preparar e executar as instruções sql
    PreparedStatement pst = null;
    ResultSet rs = null;
    //a linha abaixo cria uma variavel para armazenar um texto de acordo com o radion button selecionado
    private String tipo;    
    
    
    
    
    //Creates new form TelaOS  
    public TelaOS() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    
    private void pesquisar_cliente(){
        String sql = "SELECT id_cli as ID, nome_cli as Nome, fone_cli as Telefone FROM ci_client WHERE nome_cli LIKE ?";
        try {
            conexao = ModuloConexao.conector();
            pst = conexao.prepareStatement(sql);
            //passando o conteudo da caixa de pesquisa para o ?
            //atenção ao % - continuação da string sql
            pst.setString(1, txtPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //metodo para setar os campos do formulario com o conteudo da tabela
    public void setar_campos(){
        int setar = tblClientes.getSelectedRow();
        txtID.setText(tblClientes.getModel().getValueAt(setar, 0).toString());
    }
    
    //meotodo para cadastrar uma OS
    private void emitir_OS(){
        String sql = "INSERT INTO ci_os (tipo,situacao,equipamento,marca,modelo,acessorios,defeito,defeito_constatado,servico_executado,tecnico,valor,id_cli) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            conexao = ModuloConexao.conector();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboOsSit.getSelectedItem().toString());
            pst.setString(3, txtEquipamento.getText());
            pst.setString(4, txtMarca.getText());
            pst.setString(5, txtModelo.getText());
            pst.setString(6, txtAcessorios.getText());
            pst.setString(7, txtDefeito.getText());
            pst.setString(8, txtDConstatado.getText());
            pst.setString(9, txtServExecutado.getText());
            pst.setString(10, txtTecnico.getText());
            pst.setString(11, txtValor.getText().replace(",", "."));
            pst.setString(12, txtID.getText());
            
            //validação dos campos obrigatorios
            if ((txtID.getText().isEmpty()) || (txtDefeito.getText().isEmpty()) || cboOsSit.getSelectedItem().equals(" ")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0){
                    JOptionPane.showMessageDialog(null, "OS Emitida com Sucesso!");
                    //recuperar o numero da os
                    recuperar_os();
                    btnCriarOS.setEnabled(false);
                    btnBuscaOS.setEnabled(false);
                    btnImprimeOS.setEnabled(true);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    
    //meotodo para pesquisar uma os
    private void pesquisar_os(){
        //a linha abaixo cria uma caixa de entrada do tipo Joptionpane
        String num_os = JOptionPane.showInputDialog("Numero da OS");
        String sql = "SELECT os,DATE_FORMAT(data_os, '%d/%m/%Y - %H:%i'),tipo,situacao,equipamento,modelo,marca,acessorios,defeito,defeito_constatado,servico_executado,tecnico,valor,id_cli FROM ci_os WHERE os = " + num_os;
        try {
            conexao = ModuloConexao.conector();
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                txtOS.setText(rs.getString(1));
                txtData.setText(rs.getString(2));

                //setando os radio buttons
                String rbtTipo = rs.getString(3);
                if (rbtTipo.equals("Ordem de Serviço")) {
                    rbtnOS.setSelected(true);
                    tipo = "Ordem de Serviço";
                } else {
                    rbtnOrcamento.setSelected(true);
                    tipo = "Orçamento";
                }
                cboOsSit.setSelectedItem(rs.getString(4));
                txtEquipamento.setText(rs.getString(5));
                txtMarca.setText(rs.getString(6));
                txtModelo.setText(rs.getString(7));
                txtAcessorios.setText(rs.getString(8));
                txtDefeito.setText(rs.getString(9));
                txtDConstatado.setText(rs.getString(10));
                txtServExecutado.setText(rs.getString(11));
                txtTecnico.setText(rs.getString(12));
                txtValor.setText(rs.getString(13));
                txtID.setText(rs.getString(14));
                
                //evitando problemas
                btnCriarOS.setEnabled(false);
                btnBuscaOS.setEnabled(false);
                txtPesquisar.setEnabled(false);
                tblClientes.setVisible(false);
                //ativar demais botões
                btnAlteraOS.setEnabled(true);
                btnExcluiOS.setEnabled(true);
                btnImprimeOS.setEnabled(true);
                
                
            } else {
                JOptionPane.showMessageDialog(null, "OS não cadastrada!");
            }
        } catch (java.sql.SQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "OS Inválida");
            //System.out.println(e);
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, e2);
        }
    }
    
    
    //metodo para alterar um OS
    private void alterar_os() {
        String sql = "UPDATE ci_os SET tipo=?,situacao=?,equipamento=?,modelo=?,marca=?,acessorios=?,defeito=?,defeito_constatado=?,servico_executado=?,tecnico=?,valor=? WHERE os=?";
        try {
            conexao = ModuloConexao.conector();
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboOsSit.getSelectedItem().toString());
            pst.setString(3, txtEquipamento.getText());
            pst.setString(4, txtMarca.getText());
            pst.setString(5, txtModelo.getText());
            pst.setString(6, txtAcessorios.getText());
            pst.setString(7, txtDefeito.getText());
            pst.setString(8, txtDConstatado.getText());
            pst.setString(9, txtServExecutado.getText());
            pst.setString(10, txtTecnico.getText());
            pst.setString(11, txtValor.getText().replace(",", "."));
            pst.setString(12, txtOS.getText());
            
            //validação dos campos obrigatorios
            if ((txtID.getText().isEmpty()) || (txtDefeito.getText().isEmpty()) || cboOsSit.getSelectedItem().equals(" ")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0){
                    JOptionPane.showMessageDialog(null, "OS Alterada com Sucesso!");
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
    
    //metodo para excluir uma os
    private void excluir_os() {
        //conexao = ModuloConexao.conector();
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir esta OS?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM ci_os WHERE os=?";
            try {
                pst = conexao.prepareStatement(sql);
                
                pst.setString(1, txtOS.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "OS Excluida com sucesso!");
                    
                    limpar();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    
    //metodo para imprimir uma os
    private void imprimir_os() {
        conexao = ModuloConexao.conector();
        // imprimindo uma os
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impressão da OS?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            //imprimindo com o framework JasperReport
            try {
                //usando a classe HashMap para criar um filtro
                HashMap filtro = new HashMap();
                filtro.put("os", Integer.parseInt(txtOS.getText()));
                //usando a classe JasperPrint  para preparar a impressão
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/os.jasper"), filtro, conexao);
                //a linha abaixo exibe o relatório através da classe JasperViewer
                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    //recuperar os
    private void recuperar_os(){
        String sql = "SELECT MAX(os) FROM ci_os";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtOS.setText(rs.getString(1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
    //limpar os campos e gerenciar os botoes
    private void limpar() {
        //limpar os campos
        txtOS.setText(null);
        txtData.setText(null);
        txtPesquisar.setText(null);
        ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);
        cboOsSit.setSelectedItem(" ");
        txtEquipamento.setText(null);
        txtMarca.setText(null);;
        txtModelo.setText(null);
        txtAcessorios.setText(null);
        txtDefeito.setText(null);
        txtDConstatado.setText(null);
        txtServExecutado.setText(null);
        txtTecnico.setText(null);
        txtValor.setText(null);

        //habilitar objetos
        btnCriarOS.setEnabled(true);
        btnBuscaOS.setEnabled(true);
        txtPesquisar.setEnabled(true);
        tblClientes.setVisible(true);
        
        btnAlteraOS.setEnabled(false);
        btnExcluiOS.setEnabled(false);
        btnImprimeOS.setEnabled(false);
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtOS = new javax.swing.JTextField();
        txtData = new javax.swing.JTextField();
        rbtnOrcamento = new javax.swing.JRadioButton();
        rbtnOS = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        cboOsSit = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        txtPesquisar = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        txtValor = new javax.swing.JTextField();
        btnCriarOS = new javax.swing.JButton();
        btnBuscaOS = new javax.swing.JButton();
        btnAlteraOS = new javax.swing.JButton();
        btnExcluiOS = new javax.swing.JButton();
        btnImprimeOS = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDefeito = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtDConstatado = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtServExecutado = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        txtEquipamento = new javax.swing.JTextField();
        txtMarca = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtModelo = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtAcessorios = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtTecnico = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("OS");
        setPreferredSize(new java.awt.Dimension(960, 710));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Nº OS");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Data");

        txtOS.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txtOS.setEnabled(false);

        txtData.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txtData.setEnabled(false);

        buttonGroup1.add(rbtnOrcamento);
        rbtnOrcamento.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnOrcamento.setText("Orçamento");
        rbtnOrcamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnOrcamentoActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtnOS);
        rbtnOS.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        rbtnOS.setText("Ordem de Serviço");
        rbtnOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnOSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(rbtnOrcamento)
                        .addGap(18, 18, 18)
                        .addComponent(rbtnOS))
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOS, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOS, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnOrcamento)
                    .addComponent(rbtnOS))
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Situação da Ordem");

        cboOsSit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cboOsSit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Aguardando Orçamento", "Aguardando Autorização", "Autorizado", "Sem Defeito", "Serviço Concluido", "Garantia", "Entregue", "Em Reparo", "Aparelho Abandonado", "Orçamento Não Aprovado", "Vendido", "Aguardando Peças" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        txtPesquisar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyReleased(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/techos/icones/search (3).png"))); // NOI18N

        tblClientes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblClientes.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Nome", "Telefone"
            }
        ));
        tblClientes.setFocusable(false);
        tblClientes.getTableHeader().setReorderingAllowed(false);
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        tblClientes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblClientesKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        jLabel13.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel13.setText("* ID");

        txtID.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        txtID.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(txtPesquisar, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Valor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        txtValor.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        txtValor.setForeground(new java.awt.Color(51, 153, 0));
        txtValor.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtValor, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtValor)
                .addContainerGap())
        );

        btnCriarOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/techos/icones/add.png"))); // NOI18N
        btnCriarOS.setToolTipText("Criar OS");
        btnCriarOS.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCriarOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriarOSActionPerformed(evt);
            }
        });

        btnBuscaOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/techos/icones/read.png"))); // NOI18N
        btnBuscaOS.setToolTipText("Consultar OS");
        btnBuscaOS.setPreferredSize(new java.awt.Dimension(80, 80));
        btnBuscaOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscaOSActionPerformed(evt);
            }
        });

        btnAlteraOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/techos/icones/edit.png"))); // NOI18N
        btnAlteraOS.setToolTipText("Alterar OS");
        btnAlteraOS.setEnabled(false);
        btnAlteraOS.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAlteraOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlteraOSActionPerformed(evt);
            }
        });

        btnExcluiOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/techos/icones/remove.png"))); // NOI18N
        btnExcluiOS.setToolTipText("Excluir OS");
        btnExcluiOS.setEnabled(false);
        btnExcluiOS.setPreferredSize(new java.awt.Dimension(80, 80));
        btnExcluiOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluiOSActionPerformed(evt);
            }
        });

        btnImprimeOS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/techos/icones/printer.png"))); // NOI18N
        btnImprimeOS.setToolTipText("Imprimir OS");
        btnImprimeOS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImprimeOS.setEnabled(false);
        btnImprimeOS.setPreferredSize(new java.awt.Dimension(80, 80));
        btnImprimeOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimeOSActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("* Defeito Informado");

        txtDefeito.setColumns(20);
        txtDefeito.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtDefeito.setRows(4);
        jScrollPane2.setViewportView(txtDefeito);

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Defeito Constatado");

        txtDConstatado.setColumns(20);
        txtDConstatado.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtDConstatado.setRows(4);
        jScrollPane3.setViewportView(txtDConstatado);

        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setText("Serviço Executado");

        txtServExecutado.setColumns(20);
        txtServExecutado.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtServExecutado.setRows(4);
        jScrollPane4.setViewportView(txtServExecutado);

        jLabel14.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel14.setText("Equipamento");

        txtEquipamento.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtMarca.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel15.setText("Marca");

        jLabel16.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel16.setText("Modelo");

        txtModelo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel17.setText("Acessórios");

        txtAcessorios.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setText("Técnico Responsável");

        txtTecnico.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(cboOsSit, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel15)
                                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16)
                                    .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17)
                                    .addComponent(txtAcessorios, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCriarOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuscaOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAlteraOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnExcluiOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnImprimeOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel7)
                                .addComponent(jLabel5)
                                .addComponent(jScrollPane2)
                                .addComponent(jLabel6)
                                .addComponent(jScrollPane3)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(29, 29, 29))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 12, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cboOsSit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEquipamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtModelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAcessorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTecnico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCriarOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscaOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAlteraOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExcluiOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnImprimeOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 24, Short.MAX_VALUE))))
        );

        setBounds(0, 0, 1039, 675);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyReleased
        // chamando o metodo pesquisar cliente
        pesquisar_cliente();
    }//GEN-LAST:event_txtPesquisarKeyReleased

    private void tblClientesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblClientesKeyReleased
        //

    }//GEN-LAST:event_tblClientesKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // chamando o metodo setar campos
        setar_campos();
    }//GEN-LAST:event_tblClientesMouseClicked

    private void rbtnOrcamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnOrcamentoActionPerformed
        // atribuindo um texto a variavel tipo se selecionado
        tipo = "Orçamento";
    }//GEN-LAST:event_rbtnOrcamentoActionPerformed

    private void rbtnOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnOSActionPerformed
        // atribuindo um texto a variavel tipo se selecionado
        tipo = "Ordem de Serviço";
    }//GEN-LAST:event_rbtnOSActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // ao abrir o form marcar o radio button Orçamento
        rbtnOrcamento.setSelected(true);
        tipo = "Orçamento";
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnCriarOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriarOSActionPerformed
        // chamando a função emitir os
        emitir_OS();
    }//GEN-LAST:event_btnCriarOSActionPerformed

    private void btnBuscaOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscaOSActionPerformed
        //chama o metodo pesquisar
        pesquisar_os();
    }//GEN-LAST:event_btnBuscaOSActionPerformed

    private void btnAlteraOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlteraOSActionPerformed
        // chamando o metodo alterar OS
        alterar_os();
    }//GEN-LAST:event_btnAlteraOSActionPerformed

    private void btnExcluiOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluiOSActionPerformed
        // chamando o metodo para excluir os
        excluir_os();
    }//GEN-LAST:event_btnExcluiOSActionPerformed

    private void btnImprimeOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimeOSActionPerformed
        //chamando o metodo para imprimir uma os
        imprimir_os();
    }//GEN-LAST:event_btnImprimeOSActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlteraOS;
    private javax.swing.JButton btnBuscaOS;
    private javax.swing.JButton btnCriarOS;
    private javax.swing.JButton btnExcluiOS;
    private javax.swing.JButton btnImprimeOS;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboOsSit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JRadioButton rbtnOS;
    private javax.swing.JRadioButton rbtnOrcamento;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtAcessorios;
    private javax.swing.JTextArea txtDConstatado;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextArea txtDefeito;
    private javax.swing.JTextField txtEquipamento;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo;
    private javax.swing.JTextField txtOS;
    private javax.swing.JTextField txtPesquisar;
    private javax.swing.JTextArea txtServExecutado;
    private javax.swing.JTextField txtTecnico;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
