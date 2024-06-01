package cadastroee.servlets;

import cadastroee.model.Produto;
import cadastroee.controller.ProdutoFacadeLocal;
import java.io.IOException;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ServletProdutoFC", urlPatterns = {"/ServletProdutoFC"})
public class ServletProdutoFC extends HttpServlet {

    @EJB
    private ProdutoFacadeLocal facade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");
        String destino = handleGetAction(acao, request);
        dispatchRequest(request, response, destino);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");
        acao = acao == null || acao.isEmpty() ? " " : acao;
        String destino = handlePostAction(acao, request);
        dispatchRequest(request, response, destino);
    }

    private String handleGetAction(String acao, HttpServletRequest request) {
        if (acao == null) {
            return handleListarProdutos(request);
        }
        switch (acao) {
            case "formIncluir":
                return "ProdutoDados.jsp";
            case "excluir":
                return handleExcluir(request);
            case "formAlterar":
                return handleFormAlterar(request);
            default:
                return handleListarProdutos(request);
        }
    }

    private String handlePostAction(String acao, HttpServletRequest request) {
        if (acao == null) {
            return handleListarProdutos(request);
        }
        switch (acao) {
            case "incluir":
                return handleIncluir(request);
            case "alterar":
                return handleAlterar(request);
            default:
                return handleListarProdutos(request);
        }
    }

    private void dispatchRequest(HttpServletRequest request, HttpServletResponse response, String destino)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(destino);
        dispatcher.forward(request, response);
    }

    private String handleExcluir(HttpServletRequest request) {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int idDel = Integer.parseInt(idParam);
            Produto produto = facade.find(idDel);
            if (produto != null) {
                facade.remove(produto);
            }
        }
        return handleListarProdutos(request);
    }

    private String handleFormAlterar(HttpServletRequest request) {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Produto produto = facade.find(id);
            if (produto != null) {
                request.setAttribute("produto", produto);
            }
        }
        return "ProdutoDados.jsp";
    }

    private String handleListarProdutos(HttpServletRequest request) {
        List<Produto> produtos = facade.findAll();
        request.setAttribute("produtos", produtos);
        return "DbLista.jsp";
    }

    private String handleIncluir(HttpServletRequest request) {
        String nome = request.getParameter("nome");
        String quantidadeParam = request.getParameter("quantidade");
        String precoVendaParam = request.getParameter("precoVenda");

        if (nome != null && quantidadeParam != null && precoVendaParam != null) {
            int quantidade = Integer.parseInt(quantidadeParam);
            Float precoVenda = Float.valueOf(precoVendaParam);

            Produto newProduto = new Produto();
            newProduto.setNome(nome);
            newProduto.setQuantidade(quantidade);
            newProduto.setPrecoVenda(precoVenda);

            facade.create(newProduto);
        }
        return handleListarProdutos(request);
    }

    private String handleAlterar(HttpServletRequest request) {
        String idParam = request.getParameter("id");
        if (idParam != null) {
            Produto alterarProduto = facade.find(Integer.valueOf(idParam));

            if (alterarProduto != null) {
                String alterarNome = request.getParameter("nome");
                String alterarQuantidadeParam = request.getParameter("quantidade");
                String alterarPrecoVendaParam = request.getParameter("precoVenda");

                if (alterarNome != null && alterarQuantidadeParam != null && alterarPrecoVendaParam != null) {
                    int alterarQuantidade = Integer.parseInt(alterarQuantidadeParam);
                    Float alterarPrecoVenda = Float.valueOf(alterarPrecoVendaParam);

                    alterarProduto.setNome(alterarNome);
                    alterarProduto.setQuantidade(alterarQuantidade);
                    alterarProduto.setPrecoVenda(alterarPrecoVenda);

                    facade.edit(alterarProduto);
                }
            }
        }
        return handleListarProdutos(request);
    }
}
