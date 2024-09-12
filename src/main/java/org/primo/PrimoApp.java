package org.primo;

import org.hibernate.SessionFactory;
import org.primo.controller.PrimoController;
import org.primo.exceptions.PrimoExceptionHandler;
import org.primo.repositories.GameSpinRepository;
import org.primo.repositories.PlayerRepository;
import org.primo.service.PrimoService;
import spark.Spark;

import static org.primo.config.DataInitializer.addDBUsers;
import static org.primo.config.HibernateConfig.getSessionFactory;

public class PrimoApp {

    public static void main(String[] args) {
        SessionFactory sessionFactory = getSessionFactory();

        GameSpinRepository gameSpinRepository = new GameSpinRepository(sessionFactory);
        PlayerRepository playerRepository = new PlayerRepository(sessionFactory);

        PrimoService primoService = new PrimoService(gameSpinRepository, playerRepository);

        new PrimoController(primoService);
        new PrimoExceptionHandler();

        // Add users for testing purposes
        addDBUsers();

        Spark.init();
    }
}