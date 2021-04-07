import lesson_6.App;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@ExtendWith(SoftAssertionsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArraysTest {
    public static final String UNEXPECTED_RESULT = "Unexpected result";
    private App app;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @BeforeAll
    public void setup() {
        app = new App();
    }

    @ParameterizedTest(name = "source array: {0}; result: {1}")
    @MethodSource("getNewArrayTestData")
    public void getNewArrayTest(int[] source, int[] result) {
        softly.assertThat(app.getNewArray(source))
                .as(UNEXPECTED_RESULT)
                .isEqualTo(result);
    }

    @Test
    public void getNewArrayNegativeTest() {
        Assertions.assertThatThrownBy(() -> app.getNewArray(new int[]{1, 1, 1, 1}))
                .as(UNEXPECTED_RESULT)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid source array");
    }

    @ParameterizedTest
    @MethodSource("getCheckArrayTestData")
    public void checkArrayNegativeTest(int[] source, boolean result){
        Assertions.assertThat(app.checkArray(source))
                .as(UNEXPECTED_RESULT)
                .isFalse();
    }

    @Test
    public void checkArrayTest(){
        Assertions.assertThat(app.checkArray(new int[]{1, 4, 6, 8}))
                .as(UNEXPECTED_RESULT)
                .isTrue();
    }

    public static Stream<Arguments> getNewArrayTestData() {
        return Stream.of(
                Arguments.of(new int[]{1, 2, 3, 4, 5}, new int[]{5}),
                Arguments.of(new int[]{4, 6, 5, 4, 2, 5}, new int[]{2, 5}),
                Arguments.of(new int[]{1, 2, 3, 5, 6, 7, 4}, new int[]{})
        );
    }

    public static Stream<Arguments> getCheckArrayTestData() {
        return Stream.of(
                Arguments.of(new int[]{7, 6, 1, 9}, false),
                Arguments.of(new int[]{3, 5, 9, 4, 6}, false),
                Arguments.of(new int[]{5, 9, 7, 3}, false)
        );
    }
}
