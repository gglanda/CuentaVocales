package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static final String TEXTO =
            "En un lugar de la Mancha de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidalgo de " +
                    "los de lanza en astillero, adarga antigua, rocín flaco y galgo corredor. Una olla de algo más vaca que carnero, " +
                    "salpicón las más noches, duelos y quebrantos los sábados, lentejas los viernes, algún palomino de añadidura los " +
                    "domingos, consumían las tres partes de su hacienda. El resto della concluían sayo de velarte, calzas de velludo " +
                    "para las fiestas con sus pantuflos de lo mismo, los días de entre semana se honraba con su vellori de lo más fino. " +
                    "Tenía en su casa una ama que pasaba de los cuarenta, y una sobrina que no llegaba a los veinte, y un mozo de campo " +
                    "y plaza, que así ensillaba el rocín como tomaba la podadera. Frisaba la edad de nuestro hidalgo con los cincuenta años, " +
                    "era de complexión recia, seco de carnes, enjuto de rostro; gran madrugador y amigo de la caza. Quieren decir que tenía el " +
                    "sobrenombre de Quijada o Quesada (que en esto hay alguna diferencia en los autores que deste caso escriben), aunque por " +
                    "conjeturas verosímiles se deja entender que se llama Quijana; pero esto importa poco a nuestro cuento; basta que en la " +
                    "narración dél no se salga un punto de la verdad. Es, pues, de saber, que este sobredicho hidalgo, los ratos que estaba " +
                    "ocioso (que eran los más del año) se daba a leer libros de caballerías con tanta afición y gusto, que olvidó casi de todo " +
                    "punto el ejercicio de la caza, y aun la administración de su hacienda; y llegó a tanto su curiosidad y desatino en esto, " +
                    "que vendió muchas hanegas de tierra de sembradura, para comprar libros de caballerías en que leer; y así llevó a su casa" +
                    "todos cuantos pudo haber dellos; y de todos ningunos le parecían tan bien como los que compuso el famoso Feliciano de " +
                    "Silva: porque la claridad de su prosa, y aquellas intrincadas razones suyas, le parecían de perlas; y más cuando llegaba " +
                    "a leer aquellos requiebros y cartas de desafío, donde en muchas partes hallaba escrito: la razón de la sinrazón que a mi " +
                    "razón se hace, de tal manera mi razón enflaquece, que con razón me quejo de la vuestra fermosura, y también cuando leía: " +
                    "los altos cielos que de vuestra divinidad divinamente con las estrellas se fortifican, y os hacen merecedora del " +
                    "merecimiento que merece la vuestra grandeza.";

    public static final int NUMERO_HILOS = 3;
    public static final int NUMERO_TAREAS = 3;

    public static void main(String[] args) {

        // Es preferible usar la interfaz ExecutorService
        ExecutorService l_Executor = Executors.newFixedThreadPool(NUMERO_HILOS);
        List<Contador> l_ListaTareas = new ArrayList<>();
        List<Future<Integer>> l_ListaResultados = null;

        int l_TotalCaracteres = 0;
        int l_LongitudTexto = TEXTO.length();
        int l_CaracteresPorTarea = l_LongitudTexto / NUMERO_TAREAS;

        // Creación correcta del bucle para repartir el texto entre las tareas
        for (int i = 0; i < NUMERO_TAREAS; i++) {
            int desde = i * l_CaracteresPorTarea;
            // Asegurarnos de que la última tarea procese hasta el final exacto del texto
            // en caso de que la división no sea exacta (el resto).
            int hasta = (i == NUMERO_TAREAS - 1) ? l_LongitudTexto : desde + l_CaracteresPorTarea;

            l_ListaTareas.add(new Contador(desde, hasta));
        }

        try {
            // Ejecutamos todas las tareas
            l_ListaResultados = l_Executor.invokeAll(l_ListaTareas);

            // Recopilar los resultados requiriendo el método .get()
            for(Future<Integer> l_ResultadoTarea : l_ListaResultados) {
                l_TotalCaracteres += l_ResultadoTarea.get();
            }

            System.out.println("Total de caracteres calculados por hilos: " + l_TotalCaracteres);
            System.out.println("Total de caracteres reales (String.length): " + l_LongitudTexto);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // Siempre es buena práctica cerrar el executor en un bloque finally
            l_Executor.shutdown();
        }
    }

    // Clase estática interna para que pueda ser instanciada desde el main estático
    public static class Contador implements Callable<Integer> {
        private final int a_Desde;
        private final int a_Hasta;

        public Contador(int p_Desde, int p_Hasta) {
            this.a_Desde = p_Desde;
            this.a_Hasta = p_Hasta;
        }

        @Override
        public Integer call() {
            // Extraemos el subtexto y calculamos su longitud real
            String porcion = TEXTO.substring(a_Desde, a_Hasta);
            System.out.println("Hilo " + Thread.currentThread().getName() + " procesando desde " + a_Desde + " hasta " + a_Hasta + " (Caracteres: " + porcion.length() + ")");
            return porcion.length();
        }
    }
}