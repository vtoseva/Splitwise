package bg.sofia.uni.fmi.mjt.splitwise.repository;

import bg.sofia.uni.fmi.mjt.splitwise.entity.Transaction;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InternalErrorException;
import bg.sofia.uni.fmi.mjt.splitwise.utility.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionRepo implements Repository<String, List<Transaction>> {
    private Set<Transaction> transactions;
    private Map<String, List<Transaction>> userTransactions;

    private static TransactionRepo instance = new TransactionRepo();

    private TransactionRepo() {
        userTransactions = new HashMap<>();
        transactions = new TreeSet<>();
    }

    public static TransactionRepo getInstance() {
        return instance;
    }

    @Override
    public Collection<List<Transaction>> getAll() {
        return userTransactions.values();
    }

    @Override
    public List<Transaction> get(String from) {
        return userTransactions.get(from);
    }

    @Override
    public void insert(String from, List<Transaction> transaction) {
        userTransactions.putIfAbsent(from, transaction);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        userTransactions.putIfAbsent(transaction.from(), new ArrayList<>());
        userTransactions.get(transaction.from()).add(transaction);
    }

    @Override
    public boolean contains(String key) {
        return userTransactions.containsKey(key);
    }

    @Override
    public void delete(String from) {
        userTransactions.remove(from);
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void load(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
            Type type = new TypeToken<Set<Transaction>>() { }.getType();
            transactions = gson.fromJson(bufferedReader, type);
            if (transactions == null) {
                transactions = new TreeSet<>();
            }
            userTransactions = transactions.stream().collect(Collectors.groupingBy(Transaction::from));
        } catch (IOException e) {
            throw new InternalErrorException("Exception thrown when loading user data", e);
        }
    }

    @Override
    public void load(String path) {
        try (Reader fileReader = new FileReader(path)) {
            load(fileReader);
        } catch (IOException e) {
            throw new InternalErrorException("Exception thrown when loading user data", e);
        }
    }
    @Override
    public void save(Writer writer) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
            bufferedWriter.write(gson.toJson(transactions, Set.class));
        } catch (IOException e) {
            throw new InternalErrorException("Exception thrown when saving user data", e);
        }
    }

    @Override
    public void save(String path) {
        try (Writer fileWriter = new FileWriter(path)) {
            save(fileWriter);
        } catch (IOException e) {
            throw new InternalErrorException("Exception thrown when saving user data", e);
        }
    }
}
