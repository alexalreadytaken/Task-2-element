import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class App {

    private static final List<IElement> elements_100 = new ArrayList<>(100);
    private static final ElementExampleImpl.Context context_100 = new ElementExampleImpl.Context();

    private static final List<IElement> elements_1000 = new ArrayList<>(1000);
    private static final ElementExampleImpl.Context context_1000 = new ElementExampleImpl.Context();

    private static final List<IElement> elements_10_000 = new ArrayList<>(10_000);
    private static final ElementExampleImpl.Context context_10_000 = new ElementExampleImpl.Context();

    private static final List<IElement> elements_100_000 = new ArrayList<>(100_000);
    private static final ElementExampleImpl.Context context_100_000 = new ElementExampleImpl.Context();

    static {
        for (int i = 0; i < 100; i++)elements_100.add(new ElementExampleImpl(context_100,i));
        for (int i = 0; i < 1000; i++)elements_1000.add(new ElementExampleImpl(context_1000,i));
        for (int i = 0; i < 10_000; i++)elements_10_000.add(new ElementExampleImpl(context_10_000,i));
        for (int i = 0; i < 100_000; i++)elements_100_000.add(new ElementExampleImpl(context_100_000,i));
    }

    public static void main(String[] args) {
        String result1 = testWithShuffle(Task2Impl.INSTANCE_1, 1, elements_100, context_100);
        System.out.println(result1);

        /*String result2 = testWithShuffle(Task2Impl.INSTANCE_1, 1, elements_1000, context_1000);
        System.out.println(result2);

        String result3 = testWithShuffle(Task2Impl.INSTANCE_1, 1, elements_10_000, context_10_000);
        System.out.println(result3);

        String result4 = testWithShuffle(Task2Impl.INSTANCE_1, 1, elements_100_000, context_100_000);
        System.out.println(result4);*/
    }

    @SuppressWarnings("all")
    private static String testWithShuffle(IElementNumberAssigner assigner,int testCount,List<IElement> elements,ElementExampleImpl.Context context){
        long start = System.currentTimeMillis();
        for (int i = 0; i < testCount; i++) {
            Collections.shuffle(elements);
            assigner.assignNumbers(elements);
        }
        long end = System.currentTimeMillis();
        boolean assignerWork = IntStream.range(0, elements.size())
                .allMatch(i -> elements.get(i).getNumber() == i);
        if (!assignerWork)throw new IllegalStateException("assigner invalid :)");
        int operationCount = context.getOperationCount();
        double avgOperationsCount = (double) operationCount / testCount;
        double performance = (double) end - start;
        double avgPerformance = (double) performance / testCount;
        String a = String.format("assigner = %s \n", assigner);
        String b = String.format("time for assign %s times %s elements = %s ms , avg = %s ms \n", testCount, elements.size(), performance, avgPerformance);
        String c = String.format("operation count = %s , avg = %s \n", operationCount, avgOperationsCount);
        return a.concat(b).concat(c);
    }
}

