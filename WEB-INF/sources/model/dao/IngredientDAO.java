package model.dao;

import java.util.List;

import model.dto.Ingredient;

public interface IngredientDAO {

    public List<Ingredient> findAll();

    public Ingredient findById(int ino);

    public boolean save(Ingredient ingredient);
}
