from abc import ABC, abstractmethod
from random import randint
import tkinter as tk
from tkinter import ttk

class TP2:

    @staticmethod
    def main():
        j = JanelaPrincipal.getInstance()
        j.mainloop()

class Arquivo:
    
    instancia = None

    @staticmethod
    def obterArquivo():
        if Arquivo.instancia == None:
            Arquivo.instancia = Arquivo()
        return Arquivo.instancia


    def leArquivo(self, lp, lv):

        f = open("data.txt", "r")

        l = f.readlines()
        qtdpizzas = int(l[0])
        l.pop(0)

        for i in range(qtdpizzas):
            line = l[0].split()
            num = int(line[0])
            v = float(line[1])
            s = ' '.join(line[2:]).strip()

            pi = Pizza(s, v)
            pi.setNumero(num)

            lp.append(pi)
            l.pop(0)

        qtdvendas = int(l[0])
        l.pop(0)

        for i in range(qtdvendas):
            line = l[0].split()
            num = int(line[0])
            qtdpizzas = int(line[1])
            l.pop(0)

            p = []
            q = []

            for j in range(qtdpizzas):
                line = l[0].split()
                qtd = int(line[0])
                valor = float(line[1])
                s = ' '.join(line[2:]).strip()

                ind = 0
                encontrou = False
                for piz in lp:
                    if (piz.getSabor() == s): 
                        encontrou = True
                        break
                    ind += 1

                if encontrou:
                    p.append(lp[ind])
                else: p.append(Pizza(s, float(valor)))
                q.append(qtd)
                l.pop(0)

            vi = Venda()
            for i in range(len(p)):
                vi.adicionaVenda(p[i], q[i])        
        
            vi.setNumero(num)
            lv.append(vi)

        f.close()

    def escreveArquivo(self, lp, lv):

        f = open("data.txt", "w")

        f.write("%d\n" % len(lp))
        for p in lp:
            f.write("%d %.2f %s\n" % (p.getNumero(), p.getValor(), p.getSabor()))

        f.write("%d\n" % len(lv))
        for v in lv:
            f.write("%d %d\n" % (v.getNumero(), len(v.getPizzas())))
            i = 0
            for p in v.getPizzas():
                f.write("%d %f %s\n" % (v.getQtd()[i], p.getValor(), p.getSabor()))
                i += 1

        f.close()


class Entidade(ABC):
    def getNumero(self): return self.numero
    def setNumero(self, n): self.numero = n

class Pizza(Entidade):

    def __init__(self, sabor='', valor=0):
        self.__sabor = sabor
        self.__valor = valor
        self.setNumero(randint(0, 10000))

    def setSabor(self, sabor): self.__sabor = sabor
    def setValor(self, valor): self.__valor = valor

    def getValor(self): return self.__valor
    def getSabor(self): return self.__sabor

class Venda(Entidade):

    def __init__(self):
        self.pizzas = []
        self.qtd = []
        self.setNumero(randint(0, 10000))

    def getPizzas(self): return self.pizzas
    def getQtd(self): return self.qtd

    def calculaTotal(self):
        total = 0
        i = 0

        for p in self.pizzas:
            total += p.getValor() * self.qtd[i]
            i += 1

        return total
    
    def adicionaVenda(self, p, q):
        self.pizzas.append(p)
        self.qtd.append(q)

class Controller:

    lista_pizzas = []
    lista_vendas = []

    @staticmethod
    def adicionaPizza(pizza):
        Controller.lista_pizzas.append(pizza)
        Controller.atualiza()

    @staticmethod
    def removePizza(pizza):
        Controller.lista_pizzas.remove(pizza)
        Controller.atualiza()

    @staticmethod
    def atualiza():
        j = JanelaPrincipal.getInstance()
        for i in j.filhas[:]:
            try: i.atualiza()
            except:
                j.filhas.remove(i)

    @staticmethod
    def adicionaVenda(venda):
        Controller.lista_vendas.append(venda)
        Controller.atualiza()

    @staticmethod
    def removeVenda(venda):
        Controller.lista_vendas.pop(venda)
        Controller.atualiza()

    @staticmethod
    def carregar():
        arq = Arquivo.obterArquivo()
        arq.leArquivo(Controller.lista_pizzas, Controller.lista_vendas)
        Controller.atualiza()

    @staticmethod
    def salvar():
        arq = Arquivo.obterArquivo()
        arq.escreveArquivo(Controller.lista_pizzas, Controller.lista_vendas)
        

class AbstractFactory(ABC):

    @staticmethod
    def obterFabrica(tipo):
        if tipo == 'pizza':
            return FabricaPizza()

        elif tipo == 'venda':
            return FabricaVenda()

    @abstractmethod
    def obterJanela(self): pass

    @abstractmethod
    def obterEntidade(self): pass

class FabricaPizza(AbstractFactory):

    def obterJanela(self, tipo):
        if tipo == 'principal':
            return JanelaPizzas('Fabrica de Pizzas', [485,200])

        elif tipo == 'secundaria':
            return JanelaAdicionarPizza('Adicionar Pizzas', [200,200])

    def obterEntidade(self):
        return Pizza()

class FabricaVenda(AbstractFactory):

    def obterJanela(self, tipo):
        if tipo == 'principal':
            return JanelaVendas('Vendas', [685,200])

        elif tipo == 'secundaria':
            return JanelaAdicionarVenda('Adicionar Vendas', [300,100])

    def obterEntidade(self):
        return Venda()

class Janela(tk.Tk, ABC):

    def __init__(self, nome, tam):
        super().__init__()
        self.title(nome)
        self.protocol('WM_DELETE_WINDOW', self.destroy)
        self.geometry(f'{tam[0]}x{tam[1]}')

    @abstractmethod
    def atualiza(self): pass
        

class JanelaPrincipal(Janela):

    instance = None

    @staticmethod
    def getInstance():
        if JanelaPrincipal.instance == None:
            JanelaPrincipal.instance = JanelaPrincipal(
                'Pizzaria GigaPizza', [200,65])
        return JanelaPrincipal.instance

    def __init__(self, nome, tam):
        super().__init__(nome, tam)

        self.protocol('WM_DELETE_WINDOW', self.quit)

        self.filhas = []

        menu = tk.Menu(self)
        item = tk.Menu(menu, tearoff=0)
        item.add_command(label='Salvar dados', command=self.salvar)
        item.add_command(label='Carregar dados', command=self.carregar)
        menu.add_cascade(label='Opções', menu=item)
        self.config(menu=menu)

        tk.Button(self, text='Pizzas', command=self.menuPizzas, width=10)\
            .pack()

        tk.Button(self, text='Vendas', command=self.menuVendas,width=10)\
            .pack()

    def salvar(self):
        Controller.salvar()
    
    def carregar(self):
        Controller.carregar()

    def menuPizzas(self):
        self.filhas.append(AbstractFactory.obterFabrica('pizza')\
                            .obterJanela('principal'))

    def menuVendas(self):
        self.filhas.append(AbstractFactory.obterFabrica('venda')\
                            .obterJanela('principal'))

    def atualiza(self): pass

    def quit(self):
        for i in self.filhas:
            try: i.destroy()
            except: pass

        self.destroy()

class JanelaPizzas(Janela):

    def __init__(self, nome, tam):
        super().__init__(nome, tam)
        
        self.tree = ttk.Treeview(self, columns=['Valor'])
        self.tree['columns'] = ('#1')

        self.tree.heading('#0', text='Sabor')
        self.tree.heading('#1', text='Preço')

        for i in range(len(Controller.lista_pizzas)):
            p = Controller.lista_pizzas[i]
            self.tree.insert('', i, text=p.getSabor(), values=[p.getValor()])

        self.tree.grid(column=0, row=0)

        f = tk.Frame(self)
        tk.Button(f, text='Adicionar', command=self.adicionaSabor)\
            .grid(column=0, row=0)
        tk.Button(f, text='Remover', command=self.removeSabor)\
            .grid(column=0, row=1)

        f.grid(column=1, row=0)
    
    def adicionaSabor(self):
        j = JanelaPrincipal.getInstance()
        j.filhas.append(AbstractFactory.obterFabrica('pizza')\
            .obterJanela('secundaria'))
        
    def removeSabor(self):
        for item in self.tree.selection()[::-1]:
            Controller.removePizza(Controller.lista_pizzas[self.tree.index(item)])

    def atualiza(self):
        self.tree.destroy()

        self.tree = ttk.Treeview(self, columns=['Valor'])
        self.tree['columns'] = ('#1')

        self.tree.heading('#0', text='Sabor')
        self.tree.heading('#1', text='Preço')

        for i in range(len(Controller.lista_pizzas)):
            p = Controller.lista_pizzas[i]
            self.tree.insert('', i, text=p.getSabor(), values=[p.getValor()])

        self.tree.grid(column=0, row=0)
        
class JanelaVendas(Janela):

    def __init__(self, nome, tam):
        super().__init__(nome, tam)

        self.tree = ttk.Treeview(self, columns=['Valor'])
        self.tree['columns'] = ('#1', '#2')
    
        self.tree.heading('#0', text='Índice/Sabor')
        self.tree.heading('#1', text='Quantidade')
        self.tree.heading('#2', text='Total')

        for i in range(len(Controller.lista_vendas)):
            venda = Controller.lista_vendas[i]
            pai = self.tree.insert('', i, text=str(i+1),
                                    values=['', venda.calculaTotal()])
            for j in range(len(venda.getPizzas())):
                pizza = venda.getPizzas()[j]
                self.tree.insert(
                    pai, j, text=pizza.getSabor(), 
                    values=[
                        venda.getQtd()[j],
                        pizza.getValor()*venda.getQtd()[j]
                    ]
                )

        self.tree.grid(column=0, row=0)

        self.f = tk.Frame(self)
        tk.Button(self.f, text='Adicionar', command=self.adicionaVenda)\
            .grid(column=0, row=0)
        tk.Button(self.f, text='Remover', command=self.removeVenda)\
            .grid(column=0, row=1)

        self.f.grid(column=1, row=0)

    def adicionaVenda(self):
        j = JanelaPrincipal.getInstance()
        j.filhas.append(AbstractFactory.obterFabrica('venda')\
            .obterJanela('secundaria'))

    def removeVenda(self):
        for item in self.tree.selection()[::-1]:
            pai = self.tree.parent(item)
            if pai:
                Controller.removeVenda(self.tree.index(pai))
            else:
                Controller.removeVenda(self.tree.index(item))

    def atualiza(self):
        self.tree.destroy()
        self.f.destroy()
        self.tree = ttk.Treeview(self, columns=['Valor'])
        self.tree['columns'] = ('#1', '#2')
    
        self.tree.heading('#0', text='Índice/Sabor')
        self.tree.heading('#1', text='Quantidade')
        self.tree.heading('#2', text='Total')

        for i in range(len(Controller.lista_vendas)):
            venda = Controller.lista_vendas[i]
            pai = self.tree.insert('', i, text=str(i+1),
                                    values=['', venda.calculaTotal()])
            for j in range(len(venda.getPizzas())):
                pizza = venda.getPizzas()[j]
                self.tree.insert(
                    pai, j, text=pizza.getSabor(), 
                    values=[
                        venda.getQtd()[j],
                        pizza.getValor()*venda.getQtd()[j]
                    ]
                )

        self.tree.grid(column=0, row=0)

        self.f = tk.Frame(self)
        tk.Button(self.f, text='Adicionar', command=self.adicionaVenda)\
            .grid(column=0, row=0)
        tk.Button(self.f, text='Remover', command=self.removeVenda)\
            .grid(column=0, row=1)

        self.f.grid(column=1, row=0)

class JanelaAdicionarPizza(Janela):

    def __init__(self, nome, tam):
        super().__init__(nome, tam)

        tk.Label(self, text='Sabor:').grid(column=0, row=0)
        tk.Label(self, text='Preço:').grid(column=0, row=1)
        
        self.sabor = tk.Entry(self)
        self.preco = tk.Entry(self, width=4)

        self.sabor.grid(column=1, row=0)
        self.preco.grid(column=1, row=1)

        tk.Button(self, text='Confirmar', command=self.confirmar)\
            .grid(column=0, row=2)

    def confirmar(self):
        pizza = AbstractFactory.obterFabrica('pizza').obterEntidade()
        pizza.setSabor(self.sabor.get())
        pizza.setValor(float(self.preco.get()))
        Controller.adicionaPizza(pizza)
        self.destroy()

    def atualiza(self): pass

class JanelaAdicionarVenda(Janela):

    def __init__(self, nome, tam):
        super().__init__(nome, tam)

        tk.Button(self, text='Confirmar', command=self.confirmar, width=20)\
            .grid(column=0, row=0)

        temp = []
        for i in Controller.lista_pizzas:
            temp.append(i.getSabor())
        self.entradas = [ttk.Combobox(self, values=temp)]
        self.qtds = [ttk.Entry(self, width=5)]
        self.entradas[0].grid(column=0, row=1)
        self.qtds[0].grid(column=1, row=1)

        tk.Button(self, text='+', command=self.mais, width=3)\
            .grid(column=1, row=0)

    def confirmar(self):
        venda = AbstractFactory.obterFabrica('venda').obterEntidade()
        for i in range(len(self.entradas)):
            for j in Controller.lista_pizzas:
                if j.getSabor() == self.entradas[i].get():
                    venda.adicionaVenda(j, float(self.qtds[i].get()))
                    break
        
        Controller.adicionaVenda(venda)
        self.destroy()

    
    def mais(self):
        temp = []
        for i in Controller.lista_pizzas:
            temp.append(i.getSabor())
        self.entradas.append(ttk.Combobox(self, values=temp))
        self.qtds.append(ttk.Entry(self, width=5))
        self.entradas[-1].grid(column=0, row=len(self.entradas))
        self.qtds[-1].grid(column=1, row=len(self.qtds))

    def atualiza(self): pass



if __name__ == "__main__": TP2.main()