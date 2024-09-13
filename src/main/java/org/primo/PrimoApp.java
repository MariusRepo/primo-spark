package org.primo;

import org.hibernate.SessionFactory;
import org.primo.controller.PrimoController;
import org.primo.exceptions.PrimoExceptionHandler;
import org.primo.repositories.GameSpinRepository;
import org.primo.repositories.PlayerRepository;
import org.primo.service.PrimoService;
import spark.Spark;

import static org.primo.config.DataInitializer.insertDBData;
import static org.primo.config.HibernateConfig.getSessionFactory;

public class PrimoApp {

    public static void main(String[] args) {
        SessionFactory sessionFactory = getSessionFactory();

        GameSpinRepository gameSpinRepository = new GameSpinRepository(sessionFactory);
        PlayerRepository playerRepository = new PlayerRepository(sessionFactory);
        new PrimoController(new PrimoService(gameSpinRepository, playerRepository));
        new PrimoExceptionHandler();

        insertDBData(gameSpinRepository, playerRepository); // Add users for testing purposes
        Spark.init();
    }
}