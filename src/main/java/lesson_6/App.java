package lesson_6;

import java.util.Arrays;
import java.util.Objects;

public class App {
    public static void main(String[] args) {
        App app = new App();
        System.out.println(Arrays.toString(app.getNewArray(new int[]{1, 4, 3, 6, 4, 2, 1})));
        System.out.println(app.checkArray(new int[]{5,4,3,1,7}));

    }

    //    Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
//    Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
//    идущих после последней четверки.
//    Входной массив должен содержать хотя бы одну четверку, иначе в методе необходимо выбросить RuntimeException.
//    Написать набор тестов для этого метода (по 3-4 варианта входных данных). Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
    public int[] getNewArray(int[] array) {
        Integer indexOfLastFour = null;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 4) {
                indexOfLastFour = i;
            }
        }
        if (Objects.isNull(indexOfLastFour)) {
            throw new RuntimeException("Invalid source array");
        }

        return Arrays.copyOfRange(array, indexOfLastFour + 1, array.length);
    }

//    3. Написать метод, который проверяет состав массива из чисел 1 и 4.
//    Если в нем нет хоть одной четверки или единицы, то метод вернет false;
//    Написать набор тестов для этого метода (по 3-4 варианта входных данных).

    public boolean checkArray(int[] array){
        boolean doesContainOne = false;
        boolean doesContainFour = false;
        for (int i = 0; i < array.length; i++) {
            if(array[i] == 1){
                doesContainOne = true;
            }
            if(array[i] == 4){
                doesContainFour = true;
            }
        }
        return doesContainOne && doesContainFour;
    }
}
