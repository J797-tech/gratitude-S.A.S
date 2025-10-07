import java.util.Stack;

public class InvertirPila {
    public static void main(String[] args) {
        // Crear una pila original y agregar elementos
        Stack<Integer> pila = new Stack<>();
        pila.push(1);
        pila.push(2);
        pila.push(3);
        pila.push(4);
        pila.push(5);

        System.out.println("Pila original: " + pila);

        // Invertir la pila
        invertirPila(pila);

        System.out.println("Pila invertida: " + pila);
    }

    public static void invertirPila(Stack<Integer> pila) {
        // Crear una pila temporal
        Stack<Integer> pilaTemporal = new Stack<>();

        // Desapilar todos los elementos de la pila original y apilarlos en la pila temporal
        while (!pila.isEmpty()) {
            pilaTemporal.push(pila.pop());
        }

        // Ahora desapilamos de la pila temporal y lo apilamos de nuevo en la pila original
        while (!pilaTemporal.isEmpty()) {
            pila.push(pilaTemporal.pop());
        }
    }
}


