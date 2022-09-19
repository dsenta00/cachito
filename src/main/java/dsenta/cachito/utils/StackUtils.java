package dsenta.cachito.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Stack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StackUtils {

    @SuppressWarnings("unchecked cast")
    public static <T> Stack<T> cloneReversedStack(Stack<T> stack) {
        var clonedResourceStack = (Stack<T>) stack.clone();
        var reversedStack = new Stack<T>();

        while (!clonedResourceStack.isEmpty()) {
            reversedStack.push(clonedResourceStack.pop());
        }

        return reversedStack;
    }
}