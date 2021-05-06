import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;


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

    /**
     * алгоритм находит замкнутые цепи если они есть и работает внутри них
     * <blockquote>i-0 === n-2; 1.1 - цепь началась</blockquote>
     * <blockquote>i-1 === n-3; </blockquote>
     * <blockquote>i-2 === n-(-1);1.3 - кандидат найден, так как -1 точно не занята ставим ему 2,
     * -1 временно ставим (i-5) элементу</blockquote>
     * <blockquote>i-3 === n-4; </blockquote>
     * <blockquote>i-4 === n-1; </blockquote>
     * <blockquote>i-5 === n-0; 1.2 - элементу ставится {@link Task2Impl#buffer},
     * нулевому элементу ставится 0, 2 остается свободной,ищем кандидата на присвоение двойки</blockquote>
     * <blockquote>i-6 === n-5; </blockquote>
     */
    //так и не смог пофиксить невозможность работы с отрицательными числами
    POSITIVE_NUMBERS_FIXER{
        @Override
        protected void fix(List<IElement> elements, IElement element, int index) {
            //нужен что-бы не было stackoverflow при цепи больше 1024 элементов
            Stack<Consumer<Void>> stack = new Stack<>();
            if (element!=null){
                for (int i = 0; i < elements.size(); i++) {
                    IElement innerElem = elements.get(i);
                    if (innerElem.getNumber() == index) {
                        innerElem.setupNumber(super.buffer);
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
                        stack.push((unusedArgument)->fixBuffer(elements,element,index,unusedNumber0,stack));
                    }
                    return;
                }
            }
            element.setupNumber(unusedNumber);
        }
    },
    /**
     * просто проходится по списку меняя пару где есть неправильное число
     */
    ALL_NUMBERS_FIXER{
        @Override
        protected void fix(List<IElement> elements, IElement element, int index) {
            elements.stream()
                    .parallel()
                    .filter(elem -> elem.getNumber() == index && elem != element)
                    .findFirst()
                    .ifPresentOrElse(elem -> {
                        int number = element.getNumber();
                        elem.setupNumber(super.buffer);
                        element.setupNumber(index);
                        elem.setupNumber(number);
                    }, () -> element.setupNumber(index));
        }
    };

    private Integer buffer;

    public static IElementNumberAssigner getInstance(List<IElement> elements){
        boolean containsNegativeNumbers = elements.stream()
                .anyMatch(el -> 0 > el.getNumber());
        if (containsNegativeNumbers){
            System.err.println("list contains negative numbers,performance will slow down :(");
            return ALL_NUMBERS_FIXER;
        }else {
            return POSITIVE_NUMBERS_FIXER;
        }
    }

    @Override
    public void assignNumbers(final List<IElement> elements) {
        if (elements == null) return;
        makeBuffer(elements);
        for (int i = 0; i < elements.size(); i++) {
            IElement element = elements.get(i);
            if (element.getNumber() != i) {
                fix(elements,element,i);
            }
        }
    }

    protected void fix(List<IElement> elements, IElement element, int index){
        System.err.println("no default realization");
    }

    protected void makeBuffer(List<IElement> elements){
        Set<Integer> numbers = elements.stream()
                .parallel()
                .map(IElement::getNumber)
                .collect(toSet());
        int possibleBuffer = Integer.MIN_VALUE;
        do {
            ++possibleBuffer;
        }while (numbers.contains(possibleBuffer));
        buffer = possibleBuffer;
    }

}


