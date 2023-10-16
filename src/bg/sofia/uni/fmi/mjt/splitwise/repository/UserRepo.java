package bg.sofia.uni.fmi.mjt.splitwise.repository;

import bg.sofia.uni.fmi.mjt.splitwise.entity.UserProfile;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InternalErrorException;
import bg.sofia.uni.fmi.mjt.splitwise.utility.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserRepo implements Repository<String, UserProfile> {
    private Map<String, UserProfile> users;
    private static UserRepo instance = new UserRepo();

    private UserRepo() {
        users = new HashMap<>();
    }

    public static UserRepo getInstance() {
        return instance;
    }

    @Override
    public Collection<UserProfile> getAll() {
        return users.values();
    }

    @Override
    public UserProfile get(String username) {
        return users.get(username);
    }

    @Override
    public void insert(String username, UserProfile user) {
        users.putIfAbsent(username, user);
    }

    @Override
    public boolean contains(String username) {
        return users.containsKey(username);
    }

    @Override
    public void delete(String username) {
        users.remove(username);
    }

    public Map<String, UserProfile> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserProfile> users) {
        this.users = users;
    }

    @Override
    public void load(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
            Type type = new TypeToken<Map<String, UserProfile>>() {
            }.getType();
            users = gson.fromJson(bufferedReader, type);
            if (users == null) {
                users = new HashMap<>();
            }
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
            bufferedWriter.write(gson.toJson(users, Map.class));
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
