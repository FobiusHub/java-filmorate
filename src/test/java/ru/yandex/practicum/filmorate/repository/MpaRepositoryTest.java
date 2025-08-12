package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRepository.class, MpaRowMapper.class})
public class MpaRepositoryTest {
    private final MpaStorage mpaStorage;

    @Test
    public void testFindMpaById() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Mpa mpaDb = mpaStorage.get(1);

        assertThat(mpa)
                .usingRecursiveComparison()
                .isEqualTo(mpaDb);
    }

    @Test
    public void testGetAllMpas() {
        List<Mpa> mpas = new ArrayList<>();

        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");
        mpas.add(mpa1);

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");
        mpas.add(mpa2);

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");
        mpas.add(mpa3);

        Mpa mpa4 = new Mpa();
        mpa4.setId(4);
        mpa4.setName("R");
        mpas.add(mpa4);

        Mpa mpa5 = new Mpa();
        mpa5.setId(5);
        mpa5.setName("NC-17");
        mpas.add(mpa5);

        List<Mpa> mpasDb = mpaStorage.getAll();

        assertThat(mpas)
                .usingRecursiveComparison()
                .isEqualTo(mpasDb);
    }
}