package arena;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тесты для класса UnitData
 * Проверяют правильность хранения данных и вычислений
 */
public class UnitDataTest {

    private UnitData unit;

    @BeforeEach
    void setUp() {
        // Создаём тестового юнита перед каждым тестом
        unit = new UnitData("Test_Unit", "Тестовый юнит");
    }

    @Test
    @DisplayName("Проверка конструктора и геттеров")
    void testConstructor() {
        assertEquals("Test_Unit", unit.getInternalName(), "Внутреннее имя не совпадает");
        assertEquals("Тестовый юнит", unit.getDisplayName(), "Отображаемое имя не совпадает");
    }

    @Test
    @DisplayName("Проверка сеттеров и геттеров")
    void testSettersAndGetters() {
        // Устанавливаем значения
        unit.setStatPri1(10);
        unit.setStatPri2(20);
        unit.setStatSec1(30);
        unit.setStatSec2(40);
        unit.setStatPriArmour1(5);
        unit.setStatPriArmour2(6);
        unit.setStatPriArmour3(7);
        unit.setStatPri4(150);

        // Проверяем
        assertEquals(10, unit.getStatPri1(), "statPri1");
        assertEquals(20, unit.getStatPri2(), "statPri2");
        assertEquals(30, unit.getStatSec1(), "statSec1");
        assertEquals(40, unit.getStatSec2(), "statSec2");
        assertEquals(5, unit.getStatPriArmour1(), "statPriArmour1");
        assertEquals(6, unit.getStatPriArmour2(), "statPriArmour2");
        assertEquals(7, unit.getStatPriArmour3(), "statPriArmour3");
        assertEquals(150, unit.getStatPri4(), "statPri4");
    }

    @Test
    @DisplayName("Проверка вычисления общей атаки")
    void testGetTotalAttack() {
        // Атака = statPri1 + statPri2 + statSec1 + statSec2
        unit.setStatPri1(7);
        unit.setStatPri2(3);
        unit.setStatSec1(4);
        unit.setStatSec2(4);

        int expected = 7 + 3 + 4 + 4; // = 18
        assertEquals(expected, unit.getTotalAttack(), "Общая атака рассчитана неверно");
    }

    @Test
    @DisplayName("Проверка вычисления общей защиты")
    void testGetTotalDefense() {
        // Защита = statPriArmour1 + statPriArmour2 + statPriArmour3
        unit.setStatPriArmour1(1);
        unit.setStatPriArmour2(4);
        unit.setStatPriArmour3(2);

        int expected = 1 + 4 + 2; // = 7
        assertEquals(expected, unit.getTotalDefense(), "Общая защита рассчитана неверно");
    }

    @Test
    @DisplayName("Проверка наличия метательного оружия")
    void testHasRangedWeapon() {
        // Дальность > 0 → есть метательное оружие
        unit.setStatPri4(140);
        assertTrue(unit.hasRangedWeapon(), "Дальность 140 → должно быть метательное оружие");

        // Дальность == 0 → нет метательного оружия
        unit.setStatPri4(0);
        assertFalse(unit.hasRangedWeapon(), "Дальность 0 → не должно быть метательного оружия");
    }

    @Test
    @DisplayName("Проверка граничных значений атаки")
    void testAttackBoundaries() {
        // Минимальные значения
        unit.setStatPri1(1);
        unit.setStatPri2(1);
        unit.setStatSec1(1);
        unit.setStatSec2(1);
        assertEquals(4, unit.getTotalAttack(), "Минимальная атака должна быть 4");

        // Максимальные значения (по условию 1-100 для каждого поля)
        unit.setStatPri1(100);
        unit.setStatPri2(100);
        unit.setStatSec1(100);
        unit.setStatSec2(100);
        assertEquals(400, unit.getTotalAttack(), "Максимальная атака должна быть 400");
    }

    @Test
    @DisplayName("Проверка граничных значений защиты")
    void testDefenseBoundaries() {
        // Минимальные значения
        unit.setStatPriArmour1(1);
        unit.setStatPriArmour2(1);
        unit.setStatPriArmour3(1);
        assertEquals(3, unit.getTotalDefense(), "Минимальная защита должна быть 3");

        // Максимальные значения (по условию 1-100 для каждого поля)
        unit.setStatPriArmour1(100);
        unit.setStatPriArmour2(100);
        unit.setStatPriArmour3(100);
        assertEquals(300, unit.getTotalDefense(), "Максимальная защита должна быть 300");
    }

    @Test
    @DisplayName("Проверка с реальными данными Arab_Archer")
    void testRealDataArabArcher() {
        unit.setStatPri1(7);
        unit.setStatPri2(3);
        unit.setStatSec1(4);
        unit.setStatSec2(4);
        unit.setStatPriArmour1(1);
        unit.setStatPriArmour2(4);
        unit.setStatPriArmour3(2);

        assertEquals(18, unit.getTotalAttack(), "Атака Arab_Archer должна быть 18");
        assertEquals(7, unit.getTotalDefense(), "Защита Arab_Archer должна быть 7");
    }

    @Test
    @DisplayName("Проверка с реальными данными Arab_Medium_Cavalry")
    void testRealDataArabMediumCavalry() {
        unit.setStatPri1(9);
        unit.setStatPri2(10);
        unit.setStatSec1(8);
        unit.setStatSec2(6);
        unit.setStatPriArmour1(6);
        unit.setStatPriArmour2(8);
        unit.setStatPriArmour3(0);

        assertEquals(33, unit.getTotalAttack(), "Атака кавалерии должна быть 33");
        assertEquals(14, unit.getTotalDefense(), "Защита кавалерии должна быть 14");
    }
}