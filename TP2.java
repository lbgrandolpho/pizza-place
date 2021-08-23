import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.util.Random;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;

public class TP2 {

    public static void main(String args[]) {
        Controller.lista_pizza = new ArrayList<Pizza>();
        Controller.lista_venda = new ArrayList<Venda>();
        Janela j = JanelaPrincipal.getInstance("Pizzaria GigaPizza", 200, 200);
    }

}

class Arquivo {

    private static Arquivo instancia;
    public static Arquivo obterArquivo() {
        if (instancia == null)
            instancia = new Arquivo();
        return instancia;
    }

    private Arquivo() {}

    public void leArquivo(List<Pizza> lp, List<Venda> lv)
        throws IOException {
        
        File f = new File("data.txt");
        Scanner scan = new Scanner(f);
        scan.useLocale(Locale.ENGLISH);

        String s;
        double v;

        Pizza pi;
        Venda vi;

        int i, j, qtd, num, ind, qtdpizzas = scan.nextInt();

        for (i = 0; i < qtdpizzas; i++) {
            num = scan.nextInt();
            v = scan.nextDouble();
            s = scan.nextLine().strip();

            pi = new Pizza(s, v);
            pi.setNumero(num);

            lp.add(pi);
        }

        int qtdvendas = scan.nextInt();
        List<Pizza> p;
        List<Integer> q;

        for (i = 0; i < qtdvendas; i++) {
            num = scan.nextInt();
            qtdpizzas = scan.nextInt();

            p = new ArrayList<Pizza>();
            q = new ArrayList<Integer>();
            
            for (j = 0; j < qtdpizzas; j++) {
                qtd = scan.nextInt();
                double valor = scan.nextDouble();
                s = scan.nextLine().strip();

                ind = 0;
                boolean encontrou = false;
                for (Pizza piz : lp) {
                    if (piz.getSabor().compareTo(s) == 0) {
                        encontrou = true;
                        break;
                    }
                    ind++;
                }

                if (encontrou)
                    p.add(lp.get(ind));
                else p.add(new Pizza(s, valor));
                q.add(qtd);
            }

            vi = new Venda(p, q);
            vi.setNumero(num);
            lv.add(vi);
        }

        scan.close();
    }

    public void escreveArquivo(List<Pizza> lp, List<Venda> lv)
        throws IOException {
        
        FileWriter fw = new FileWriter("data.txt");
        PrintWriter pw = new PrintWriter(fw);
        
        int i;

        pw.printf("%d\n", lp.size());
        for (Pizza p : lp)
            pw.printf("%d %.2f %s\n", p.getNumero(), p.getValor(), p.getSabor());

        pw.printf("%d\n", lv.size());
        for (Venda v : lv) {
            pw.printf("%d %d\n", v.getNumero(), v.getPizzas().size());
            i = 0;
            for (Pizza p : v.getPizzas()) {
                pw.printf("%d %f %s\n", v.getQtd().get(i), p.getValor(), p.getSabor());
                i++;
            }
        }

        pw.close();

    }
    
}

abstract class Entidade {
    
    private int numero;

    public int getNumero() { return this.numero; }
    public void setNumero(int n) { numero = n; }
}

class Pizza extends Entidade {

    private String sabor;
    private double valor;

    public Pizza() {
        this.sabor = "";
        this.valor = .0f;
        setNumero((new Random()).nextInt());

    }

    public Pizza(String sabor, double valor) {
        this.sabor = sabor;
        this.valor = valor;
        setNumero((new Random()).nextInt());
    }

    public void setSabor(String sabor) { this.sabor = sabor; }
    public void setValor(double valor) { this.valor = valor; }
    public double getValor() { return valor; }
    public String getSabor() { return sabor; }

}

class Venda extends Entidade {

    public List<Pizza> pizzas;
    public List<Integer> qtd;

    public Venda() {
        pizzas = new ArrayList<Pizza>();
        qtd = new ArrayList<Integer>();
        setNumero((new Random()).nextInt());
    }

    public Venda(List<Pizza> p, List<Integer> q) {
        pizzas = p; qtd = q;
        setNumero((new Random()).nextInt());
    }

    public double calculaTotal() {
        double total = 0;
        int i = 0;
        for (Pizza p : this.pizzas)
            total += p.getValor() * qtd.get(i++);

        return total;
    }

    public List<Pizza> getPizzas() { return pizzas; }
    public List<Integer> getQtd() { return qtd; }

}

class Controller {

    public static List<Pizza> lista_pizza;
    public static List<Venda> lista_venda;
    
    public static void adicionaPizza(Pizza pizza) {
        lista_pizza.add(pizza);
        atualiza();
    }

    public static void adicionaVenda(Venda vnd) {
        lista_venda.add(vnd);
        atualiza();
    }

    public static void removePizza(Pizza pizza) {
        lista_pizza.remove(pizza);
        atualiza();
    }

    public static void removeVenda(int i) {
        lista_venda.remove(i);
        atualiza();
    }

    public static void carregar() {
        try {
            Arquivo arq = Arquivo.obterArquivo();
            arq.leArquivo(lista_pizza, lista_venda);
            atualiza();
        }
        catch (Exception e) {}
    }

    public static void salvar() {
        try {
            Arquivo arq = Arquivo.obterArquivo();
            arq.escreveArquivo(lista_pizza, lista_venda);
        }
        catch (Exception e) {}
    }

    public static void atualiza() {
        JanelaPrincipal p = JanelaPrincipal.getInstance("", 0, 0);
        for (int i = p.filhas.size()-1; i>=0; i--) {
            try {
                if (p.filhas.get(i).isDisplayable()) {
                    Janela j = p.filhas.get(i).atualiza();
                    p.filhas.remove(i);
                    p.filhas.add(j);
                }
                else p.filhas.remove(i);
            }
            catch(Exception e) {}
        }
    }
}

abstract class Janela extends JFrame {

    public Janela(String nome, int largura, int altura) {

        super(nome);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(largura, altura);

    }

    public abstract Janela atualiza(); 
}

class JanelaPrincipal extends Janela {

    private static JanelaPrincipal instance;
    public List<Janela> filhas;

    public static JanelaPrincipal getInstance(String nome, int l, int a) {

        if (instance == null)
            instance = new JanelaPrincipal(nome, l, a);
        return instance;

    }

    private JanelaPrincipal(String nome, int largura, int altura) {

        super(nome, largura, altura);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JMenuBar barra = new JMenuBar();
        JMenu menu = new JMenu("Opções");
        JMenuItem salvar = new JMenuItem("Salvar dados");
        JMenuItem carregar = new JMenuItem("Carregar dados");
        filhas = new ArrayList<Janela>();

        salvar.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    salvar();
                }
            }
        );

        carregar.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    carregar();
                }
            }
        );

        menu.add(salvar);
        menu.add(carregar);
        barra.add(menu);
        setJMenuBar(barra);

        JButton pizzas = new JButton("Pizzas");
        JButton vendas = new JButton("Vendas");

        pizzas.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    menuPizzas();
                }
            }
        );

        vendas.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    menuVendas();
                }
            }
        );

        GroupLayout a = new GroupLayout(getContentPane());
        getContentPane().setLayout(a);

        a.setHorizontalGroup(
            a.createParallelGroup(Alignment.CENTER)
                .addComponent(pizzas)
                .addComponent(vendas)
        );

        a.setVerticalGroup(
            a.createParallelGroup(Alignment.LEADING)
                .addGroup(a.createSequentialGroup()
                    .addComponent(pizzas)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(vendas)
                )
        );

        pack();
        setSize(largura, altura);
        setVisible(true);

    }

    private void carregar() {
        Controller.carregar();
    }

    private void salvar() {
        Controller.salvar();
    }

    private void menuPizzas() {
        filhas.add(AbstractFactory.obterFabrica("pizza").obterJanela("principal"));
    }
    private void menuVendas() {
        filhas.add(AbstractFactory.obterFabrica("venda").obterJanela("principal"));
    }

    public Janela atualiza() {
        return null;
    } 

}

abstract class AbstractFactory {

    public static AbstractFactory obterFabrica(String tipo) {

        if (tipo.compareTo("pizza") == 0) {
            return new FabricaPizza();
        }
        else if (tipo.compareTo("venda") == 0) {
            return new FabricaVenda();
        }
        else return null;

    }

    public abstract Janela obterJanela(String tipo);
    public abstract Entidade obterEntidade();

}

class FabricaPizza extends AbstractFactory {

    public Janela obterJanela(String tipo) {
        
        if (tipo.compareTo("principal") == 0) {
            return new JanelaPizzas("Pizzas", 200, 200);
        }
        else if (tipo.compareTo("secundaria") == 0) {
            return new JanelaAdicionarPizza("Adicionar Pizza", 200, 200);

        }
        else return null;
    }

    public Entidade obterEntidade() {
        return new Pizza();
    }
}

class FabricaVenda extends AbstractFactory {

    public Janela obterJanela(String tipo) {

        if (tipo.compareTo("principal") == 0) {
            return new JanelaVendas("Vendas", 200, 200);   
        }
        else if (tipo.compareTo("secundaria") == 0) {
            return new JanelaAdicionarVenda("Adicionar Venda", 200, 200);

        }
        else return null;
    }

    public Entidade obterEntidade() {
        return new Venda();
    }
}

class JanelaPizzas extends Janela {

    JTable arv;

    public static JanelaPizzas get() {
        return new JanelaPizzas("nome", 200, 200);
    }

    public JanelaPizzas(String nome, int largura, int altura) {
        super(nome, largura, altura);
        int i;
        Pizza pizza;
        String[] colunas = {"Sabor", "Preço"};
        Object [][] ob = new Object[Controller.lista_pizza.size()][2];
        for (i=0; i<Controller.lista_pizza.size(); i++) {
            pizza = Controller.lista_pizza.get(i);
            ob[i][0] = pizza.getSabor();
            ob[i][1] = pizza.getValor();
        }
        arv = new JTable();
        arv.setModel(new javax.swing.table.DefaultTableModel(ob, colunas) {
            public boolean isCellEditable(int ind_lin, int ind_col) {
                return false;
            }
        }
        );

        JScrollPane scrl = new JScrollPane();
        scrl.setViewportView(arv);
        
        JButton adicionar = new JButton("Adicionar");
        JButton remover = new JButton("Remover");

        adicionar.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    adicionaSabor();
                }
            }
        );

        remover.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    removeSabor();
                }
            }
        );

        GroupLayout a = new GroupLayout(getContentPane());
        getContentPane().setLayout(a);

        a.setHorizontalGroup(
            a.createSequentialGroup()
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(scrl)
                )
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(adicionar)
                    .addComponent(remover)
                )
        );

        a.setVerticalGroup(
            a.createParallelGroup(Alignment.CENTER)
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(scrl)
                )
                .addGroup(a.createSequentialGroup()
                    .addComponent(adicionar)
                    .addComponent(remover)
                )
        );

        pack();
        setVisible(true);
    }

    public void adicionaSabor() {
        JanelaPrincipal j = JanelaPrincipal.getInstance("", 0, 0);
        j.filhas.add(AbstractFactory.obterFabrica("pizza").obterJanela("secundaria"));
    }

    public void removeSabor() {
        int lin = arv.getSelectedRow(), i;
        String ind = (String)arv.getModel().getValueAt(lin, 0);
        for(i=0; i<Controller.lista_pizza.size(); i++)
            if (ind.compareTo(Controller.lista_pizza.get(i).getSabor()) == 0)
                Controller.removePizza(Controller.lista_pizza.get(i));
    }

    public Janela atualiza() {
        setVisible(false);
        dispose();
        return new JanelaPizzas("Pizzas", 200, 200);
    }

}

class JanelaVendas extends Janela {

    JTable arv;

    public static JanelaVendas get() {
        return new JanelaVendas("nome", 200, 200);
    }

    public JanelaVendas(String nome, int largura, int altura) {
        super(nome, largura, altura);
        String[] colunas = {"Índice","Quantidade","Valor Total"};
        int i=0, tam=0, k=0, j;

        for (Venda venda : Controller.lista_venda)
            tam += 1 + venda.getPizzas().size();

        Object [][] ob = new Object[tam][3];
        Pizza pizza;
        Venda venda;

        while (i<tam) {
            venda = Controller.lista_venda.get(k);
            ob[i][0] = k+1;
            ob[i][1] = "";
            ob[i][2] = venda.calculaTotal();
            i++;
        
            for (j=0; j<venda.getPizzas().size(); j++) {
                pizza = venda.getPizzas().get(j);
                ob[i+j][0] = pizza.getSabor();
                ob[i+j][1] = venda.getQtd().get(j);
                ob[i+j][2] = pizza.getValor();
            }

            i += j;
            k++;
        }


        arv = new JTable();
        arv.setModel(new javax.swing.table.DefaultTableModel(ob, colunas) {
            public boolean isCellEditable(int ind_lin, int ind_col) {
                return false;
            }
        }
        );

        JScrollPane scrl = new JScrollPane();
        scrl.setViewportView(arv);
        
        JButton adicionar = new JButton("Adicionar");
        JButton remover = new JButton("Remover");

        adicionar.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    adicionaVenda();
                }
            }
        );

        remover.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    removeVenda();
                }
            }
        );

        GroupLayout a = new GroupLayout(getContentPane());
        getContentPane().setLayout(a);

        a.setHorizontalGroup(
            a.createSequentialGroup()
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(scrl)
                )
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(adicionar)
                    .addComponent(remover)
                )
        );

        a.setVerticalGroup(
            a.createParallelGroup(Alignment.CENTER)
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(scrl)
                )
                .addGroup(a.createSequentialGroup()
                    .addComponent(adicionar)
                    .addComponent(remover)
                )
        );

        pack();
        setVisible(true);
    }

    public void adicionaVenda() {
        JanelaPrincipal j = JanelaPrincipal.getInstance("", 0, 0);
        j.filhas.add(AbstractFactory.obterFabrica("venda").obterJanela("secundaria"));
    }

    public void removeVenda() {
        int lin = arv.getSelectedRow();
        try {
            int ind = ((Integer)arv.getModel().getValueAt(lin, 0));
            Controller.removeVenda(ind-1);
        }
        catch(Exception e) {}
    }

    public Janela atualiza() {
        setVisible(false);
        dispose();
        return new JanelaVendas("Vendas", 200, 200);
    }

}

class JanelaAdicionarPizza extends Janela {

    private JTextField sabor1, valor1;

    public JanelaAdicionarPizza(String nome, int largura, int altura) {
        super(nome, largura, altura);
        JLabel sabor = new JLabel("Sabor: ");
        JLabel valor = new JLabel("Preço: ");
        sabor1 = new JTextField();
        valor1 = new JTextField();
        JButton confirmar = new JButton("Confirmar");

        confirmar.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    confirmar();
                }
            }
        );

        GroupLayout a = new GroupLayout(getContentPane());
        getContentPane().setLayout(a);

        a.setHorizontalGroup(
            a.createParallelGroup(Alignment.CENTER)
                .addGroup(a.createSequentialGroup()
                    .addGroup(a.createParallelGroup(Alignment.CENTER)
                        .addComponent(sabor)
                        .addComponent(valor)
                    )
                    .addGroup(a.createParallelGroup(Alignment.CENTER)
                        .addComponent(sabor1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(valor1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    )
                )
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(confirmar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )                    

        );

        a.setVerticalGroup(
            a.createSequentialGroup()
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addGroup(a.createSequentialGroup()
                        .addComponent(sabor)
                        .addComponent(valor)
                    )
                    .addGroup(a.createSequentialGroup()
                        .addComponent(sabor1, 0, 0, 20)
                        .addComponent(valor1, 0, 0, 20)
                    )
                ) 
                .addComponent(confirmar)
        );

        pack();
        setSize(largura, altura);
        setVisible(true);
    }

    public void confirmar() {
        Pizza pizza = (Pizza)AbstractFactory.obterFabrica("pizza").obterEntidade();
        pizza.setSabor(sabor1.getText());
        pizza.setValor(Double.parseDouble(valor1.getText()));
        dispose();
        Controller.adicionaPizza(pizza);
    }

    public Janela atualiza() {
        setVisible(false);
        dispose();
        return new JanelaAdicionarPizza("Adicionar Pizza", 200, 200);
    }

}

class JanelaAdicionarVenda extends Janela {

    private JComboBox<String>[] sabor;
    private JTextField[] qtd;
    private JButton confirmar;
    private int qtd_piz;

    public JanelaAdicionarVenda(String nome, int largura, int altura) {
        super(nome, largura, altura);
        confirmar = new JButton("Confirmar");
        int i;
        qtd_piz = 5;
        sabor = new JComboBox[qtd_piz];
        qtd = new JTextField[qtd_piz]; 
        for (i=0; i<qtd_piz; i++) {
            sabor[i] = new JComboBox<String>();
            qtd[i] = new JTextField();
            sabor[i].addItem("");
            for (Pizza pizza : Controller.lista_pizza)
                sabor[i].addItem(pizza.getSabor());
        }
    

        confirmar.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        confirmar();
                    }
                }
            );

        GroupLayout a = new GroupLayout(getContentPane());
        getContentPane().setLayout(a);

        a.setHorizontalGroup(
            a.createParallelGroup(Alignment.CENTER)
                .addGroup(a.createSequentialGroup()
                    .addGroup(a.createParallelGroup(Alignment.CENTER)
                        .addComponent(sabor[0])
                        .addComponent(sabor[1])
                        .addComponent(sabor[2])
                        .addComponent(sabor[3])
                        .addComponent(sabor[4])
                    )
                    .addGroup(a.createParallelGroup(Alignment.CENTER)
                        .addComponent(qtd[0], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(qtd[1], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(qtd[2], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(qtd[3], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(qtd[4], GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

                    )
                )
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addComponent(confirmar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                )                    

        );

        a.setVerticalGroup(
            a.createSequentialGroup()
                .addGroup(a.createParallelGroup(Alignment.CENTER)
                    .addGroup(a.createSequentialGroup()
                        .addComponent(sabor[0])
                        .addComponent(sabor[1])
                        .addComponent(sabor[2])
                        .addComponent(sabor[3])
                        .addComponent(sabor[4])
                    )
                    .addGroup(a.createSequentialGroup()
                        .addComponent(qtd[0], 0, 0, 30)
                        .addComponent(qtd[1], 0, 0, 30)
                        .addComponent(qtd[2], 0, 0, 30)
                        .addComponent(qtd[3], 0, 0, 30)
                        .addComponent(qtd[4], 0, 0, 30)
                    )
                ) 
                .addComponent(confirmar)
        );

        pack();
        setSize(largura, altura);
        setVisible(true);

    }

    public void confirmar() {
        int i, d;
        String s;
        Venda venda = (Venda)AbstractFactory.obterFabrica("venda").obterEntidade();
        List<Pizza> lp = new ArrayList<Pizza>();
        List<Integer> lv = new ArrayList<Integer>();
        for (i=0; i<qtd_piz; i++) {
            s = (String)sabor[i].getSelectedItem();
            if ("".compareTo(qtd[i].getText()) != 0) {
                d = Integer.parseInt(qtd[i].getText());
                
                for (Pizza pizza : Controller.lista_pizza)
                    if (s.compareTo(pizza.getSabor()) == 0) {
                        lp.add(pizza);
                        lv.add(d);
                }
            }
        }
        venda.pizzas = lp;
        venda.qtd = lv;

        Controller.adicionaVenda(venda);
    }

    public Janela atualiza() {
        setVisible(false);
        dispose();
        return new JanelaAdicionarVenda("Adicionar Venda", 200, 200);
    }
}