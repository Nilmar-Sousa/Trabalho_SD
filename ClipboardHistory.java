package cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

// Vamos construir a interface do usuário do nosso software Clipboard History
// Usaremos a API Swing
public class ClipboardHistory extends JPanel implements ClipboardListener.EntryListener {

    // para a lista de entradas copiadas na área de transferência
    private JList<String> list;
    private DefaultListModel<String> listModel;
    private ListSelectionModel listSelectionModel;

    public ClipboardHistory() {
        super(new BorderLayout());
        listModel = new DefaultListModel<String>();
        list = new JList<String>(listModel);
       
        listSelectionModel = list.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // criamos um JScrollPane para incorporar nosso painel de controle
        JScrollPane listPane = new JScrollPane(list);
        JPanel controlPane = new JPanel();

        // adicionamos um botão para permitir que os usuários copiem entradas antigas para a área de transferência
        final JButton button = new JButton("Copiar");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = (String) list.getSelectedValue();
                int index = list.getSelectedIndex();
                // remova o índice selecionado para evitar duplicação em nossa lista ...
                listModel.remove(index);
                // copiar para área de transferência
                copyToClipboard(value);
            }
        });

        // adicionamos o botão
        controlPane.add(button);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPane, BorderLayout.CENTER);

        JPanel topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
        JPanel listContainer = new JPanel(new GridLayout(1, 1));
        listContainer.setBorder(BorderFactory.createTitledBorder("Entradas"));
        listContainer.add(listPane);

        topHalf.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        topHalf.add(listContainer);
        topHalf.setMinimumSize(new Dimension(100, 50));
        topHalf.setPreferredSize(new Dimension(100, 250));
        splitPane.add(topHalf);

        JPanel bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.add(controlPane, BorderLayout.CENTER);
        bottomHalf.setPreferredSize(new Dimension(450, 30));
        splitPane.add(bottomHalf);
    }

    public void copyToClipboard(String value) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(value);
        clipboard.setContents(data, data);
    }

    public void createAndShowGUI() throws SocketException, UnknownHostException, IOException {
        // We create a top JFrame
        JFrame frame = new JFrame("Histórico do Clipboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setOpaque(true);
        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true); // nós exibimos na tela

        // conectamos o Ouvinte da área de transferência à nossa IU
        ClipboardListener listener = new ClipboardListener();
        listener.setEntryListener(this);
        listener.start();  
    }

    @Override
    public void onCopy(String data) {
        if(listModel.size() <= 4){
            if(listModel.contains(data)){
                int index = listModel.indexOf(data);
                listModel.remove(index);
                listModel.add(0, data);
            } else {
                listModel.add(0, data);
            }
        } else {
            if(listModel.contains(data)){
                int index = listModel.indexOf(data);
                listModel.remove(index);
                listModel.add(0, data);
            } else {
                listModel.remove(4);
                listModel.add(0, data);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new ClipboardHistory().createAndShowGUI();
                } catch (UnknownHostException ex) {
                    java.util.logging.Logger.getLogger(ClipboardHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(ClipboardHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
        });
    }

}
