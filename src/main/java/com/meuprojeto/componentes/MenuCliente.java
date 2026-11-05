package com.meuprojeto.componentes;

import com.meuprojeto.dao.ClienteDao;
import com.meuprojeto.dominio.Cliente;
import com.meuprojeto.dominio.Endereco;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public final class MenuCliente {

    // Um único DAO para todo o programa, senão os dados se perdem
    private static final ClienteDao clienteDao = new ClienteDao();
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private MenuCliente() {
    }

    public static void exibir(Scanner scanner) {
        int opcao;

        do {
            System.out.println("\n===== MENU CLIENTE =====");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Listar clientes");
            System.out.println("3 - Buscar cliente");
            System.out.println("4 - Atualizar cliente");
            System.out.println("5 - Deletar cliente");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = MenuGeral.lerOpcao(scanner);

            switch (opcao) {
                case 1 -> cadastrarCliente(scanner);
                case 2 -> listarClientes();
                case 3 -> buscarCliente(scanner);
                case 4 -> atualizarCliente(scanner);
                case 5 -> deletarCliente(scanner);
                case 0 -> System.out.println("Voltando ao menu geral...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void cadastrarCliente(Scanner scanner) {
        System.out.println("\n----- CADASTRAR CLIENTE -----");

        String nome = lerTexto(scanner, "Nome: ");

        LocalDate dataNascimento = lerData(scanner, "Data de nascimento (dd/mm/aaaa): ");
        if (dataNascimento == null) {
            System.out.println("Data inválida. Cadastro cancelado.");
            return;
        }

        String email = lerTexto(scanner, "E-mail: ");
        String telefone = lerTexto(scanner, "Telefone: ");
        String genero = lerTexto(scanner, "Gênero: ");
        String cpf = lerTexto(scanner, "CPF: ");

        System.out.println("-- Endereço --");
        String rua = lerTexto(scanner, "Rua: ");

        int numero;
        try {
            numero = Integer.parseInt(lerTexto(scanner, "Número: "));
        } catch (NumberFormatException e) {
            System.out.println("Número inválido. Cadastro cancelado.");
            return;
        }

        String cidade = lerTexto(scanner, "Cidade: ");

        Endereco endereco = new Endereco(rua, numero, cidade);

        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setDataNascimento(dataNascimento);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setGenero(genero);
        cliente.setCpf(cpf);
        cliente.setEndereco(endereco);

        clienteDao.salvar(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
    }

    private static void listarClientes() {
        System.out.println("\n----- LISTA DE CLIENTES -----");

        List<Cliente> clientes = clienteDao.listar();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }

        for (int i = 0; i < clientes.size(); i++) {
            exibirDetalhes(clientes.get(i), i);
        }
    }

    private static void buscarCliente(Scanner scanner) {
        System.out.println("\n----- BUSCAR CLIENTE -----");

        int indice;
        try {
            indice = Integer.parseInt(lerTexto(scanner, "Digite o índice do cliente: "));
        } catch (NumberFormatException e) {
            System.out.println("Índice inválido.");
            return;
        }

        Cliente cliente = clienteDao.buscar(indice);
        if (cliente == null) {
            System.out.println("Nenhum cliente encontrado com o índice " + indice + ".");
            return;
        }

        exibirDetalhes(cliente, indice);
    }

    private static void atualizarCliente(Scanner scanner) {
        System.out.println("\n----- ATUALIZAR CLIENTE -----");

        int indice;
        try {
            indice = Integer.parseInt(lerTexto(scanner, "Digite o índice do cliente: "));
        } catch (NumberFormatException e) {
            System.out.println("Índice inválido.");
            return;
        }

        Cliente cliente = clienteDao.buscar(indice);
        if (cliente == null) {
            System.out.println("Nenhum cliente encontrado com o índice " + indice + ".");
            return;
        }

        Endereco enderecoAtual = cliente.getEndereco();
        System.out.println("Deixe em branco para manter o valor atual.");

        String nome = lerTextoOpcional(scanner, "Nome (" + cliente.getNome() + "): ", cliente.getNome());

        String textoData = lerTexto(scanner,
                "Data de nascimento (" + cliente.getDataNascimento().format(FORMATO_DATA) + "): ");
        LocalDate dataNascimento = cliente.getDataNascimento();
        if (!textoData.isEmpty()) {
            dataNascimento = converterData(textoData);
            if (dataNascimento == null) {
                System.out.println("Data inválida. Atualização cancelada.");
                return;
            }
        }

        String email = lerTextoOpcional(scanner, "E-mail (" + cliente.getEmail() + "): ", cliente.getEmail());
        String telefone = lerTextoOpcional(scanner, "Telefone (" + cliente.getTelefone() + "): ", cliente.getTelefone());
        String genero = lerTextoOpcional(scanner, "Gênero (" + cliente.getGenero() + "): ", cliente.getGenero());
        String cpf = lerTextoOpcional(scanner, "CPF (" + cliente.getCpf() + "): ", cliente.getCpf());

        System.out.println("-- Endereço --");
        String rua = lerTextoOpcional(scanner, "Rua (" + enderecoAtual.getRua() + "): ", enderecoAtual.getRua());

        int numero = enderecoAtual.getNumero();
        String textoNumero = lerTexto(scanner, "Número (" + numero + "): ");
        if (!textoNumero.isEmpty()) {
            try {
                numero = Integer.parseInt(textoNumero);
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Atualização cancelada.");
                return;
            }
        }

        String cidade = lerTextoOpcional(scanner, "Cidade (" + enderecoAtual.getCidade() + "): ", enderecoAtual.getCidade());

        cliente.setNome(nome);
        cliente.setDataNascimento(dataNascimento);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setGenero(genero);
        cliente.setCpf(cpf);
        cliente.setEndereco(new Endereco(rua, numero, cidade));

        clienteDao.atualizar(indice, cliente);
        System.out.println("Cliente atualizado com sucesso!");
    }

    private static void deletarCliente(Scanner scanner) {
        System.out.println("\n----- DELETAR CLIENTE -----");

        int indice;
        try {
            indice = Integer.parseInt(lerTexto(scanner, "Digite o índice do cliente: "));
        } catch (NumberFormatException e) {
            System.out.println("Índice inválido.");
            return;
        }

        Cliente cliente = clienteDao.buscar(indice);
        if (cliente == null) {
            System.out.println("Nenhum cliente encontrado com o índice " + indice + ".");
            return;
        }

        exibirDetalhes(cliente, indice);

        String confirmacao = lerTexto(scanner, "Tem certeza que deseja deletar? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Exclusão cancelada.");
            return;
        }

        clienteDao.deletar(indice);
        System.out.println("Cliente deletado com sucesso!");
    }

    private static void exibirDetalhes(Cliente c, int indice) {
        Endereco e = c.getEndereco();

        System.out.println("\n[Índice " + indice + "]");
        System.out.println("Nome: " + c.getNome());
        System.out.println("Nascimento: " + c.getDataNascimento().format(FORMATO_DATA));
        System.out.println("E-mail: " + c.getEmail());
        System.out.println("Telefone: " + c.getTelefone());
        System.out.println("Gênero: " + c.getGenero());
        System.out.println("CPF: " + c.getCpf());
        System.out.println("Endereço: " + e.getRua() + ", " + e.getNumero() + " - " + e.getCidade());
    }

    private static String lerTexto(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    private static String lerTextoOpcional(Scanner scanner, String mensagem, String valorAtual) {
        String texto = lerTexto(scanner, mensagem);
        return texto.isEmpty() ? valorAtual : texto;
    }

    private static LocalDate lerData(Scanner scanner, String mensagem) {
        return converterData(lerTexto(scanner, mensagem));
    }

    private static LocalDate converterData(String texto) {
        try {
            return LocalDate.parse(texto, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
