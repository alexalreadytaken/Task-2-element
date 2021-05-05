import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;


/**
 * <h1>Задание №2</h1>
 * Реализуйте интерфейс {@link IElementNumberAssigner}.
 *
 * <p>Помимо качества кода, мы будем обращать внимание на оптимальность предложенного алгоритма по времени работы
 * с учетом скорости выполнения операции присвоения номера:
 * большим плюсом (хотя это и не обязательно) будет оценка числа операций, доказательство оптимальности
 * или указание области, в которой алгоритм будет оптимальным.</p>
 */
public enum Task2Impl implements IElementNumberAssigner {

    INSTANCE_1;

    private final Integer buffer = Integer.MIN_VALUE;


    @Override
    public void assignNumbers(final List<IElement> elements) {
        if (elements == null) return;
        boolean containsNegativeNumbers = elements.stream()
                .anyMatch(el -> 0 > el.getNumber());
        if (containsNegativeNumbers){
            System.err.println("list contains negative numbers,performance will slow down :(");
        }
        for (int i = 0; i < elements.size(); i++) {
            IElement element = elements.get(i);
            if (element.getNumber() != i) {
                if (containsNegativeNumbers) {
                    plainFix(elements,element,i);
                }else {
                    fixChain(elements,element,i);
                }
            }
        }
    }

    private void fixChain(List<IElement> elements, IElement element, int index){
        Stack<Consumer<Void>> stack = new Stack<>();
        if (element!=null){
            for (int i = 0; i < elements.size(); i++) {
                IElement innerElem = elements.get(i);
                if (innerElem.getNumber() == index) {
                    innerElem.setupNumber(buffer);
                    int unusedNumber = element.getNumber();
                    element.setupNumber(index);
                    int finalI = i;
                    stack.push((unusedArgument)->fixBuffer(elements,innerElem,finalI,unusedNumber,stack));
                    while (!stack.empty()){
                        stack.pop().accept(null);
                    }
                }
            }
            element.setupNumber(index);
        }
    }

    private void fixBuffer(List<IElement> elements, IElement element, int index, int unusedNumber,Stack<Consumer<Void>> stack) {
        for (int i = 0; i < elements.size(); i++) {
            if (i==unusedNumber){
                IElement innerElem = elements.get(i);
                if (innerElem.getNumber()==index){
                    innerElem.setupNumber(unusedNumber);
                    element.setupNumber(index);
                }else {
                    int unusedNumber0 = innerElem.getNumber();
                    innerElem.setupNumber(unusedNumber);
                    int finalI = i;
                    stack.push((unusedArgument)->fixBuffer(elements,innerElem,finalI,unusedNumber0,stack));
                }
                return;
            }
        }
    }

    private void plainFix(List<IElement> elements, IElement element, int index) {
        elements.stream()
                .parallel()
                .filter(elem -> elem.getNumber() == index && elem != element)
                .findFirst()
                .ifPresentOrElse(elem -> {
                    int number = element.getNumber();
                    elem.setupNumber(buffer);
                    element.setupNumber(index);
                    elem.setupNumber(number);
                }, () -> element.setupNumber(index));
    }

}


