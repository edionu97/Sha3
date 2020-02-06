package utils.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class ThreadHelper {

    private static final int THREAD_NO = Runtime.getRuntime().availableProcessors();
    private static volatile ThreadHelper instance;
    private final ExecutorService executorService;


    private ThreadHelper() {
        executorService = Executors.newFixedThreadPool(THREAD_NO);
    }

    public static ThreadHelper getInstance() {

        if (instance == null) {
            synchronized (ThreadHelper.class) {
                if (instance == null) {
                    instance = new ThreadHelper();
                }
            }
        }

        return instance;
    }

    /**
     * Execute the function on another thread
     * @param elements: the elements
     * @param threadJob: the thread job
     * @param <T> : the input data type
     * @param <U> : the output data type
     * @return a future to the result
     */
    public <T, U> Future<U> executeOnThread(final T elements,
                                            final Function<T, U> threadJob){

        return executorService.submit(() -> threadJob.apply(elements));
    }

    public <T, U> List<Future<U>> distributeOnThreadsAndSubmit(final List<T> elements,
                                                               final Function<List<T>, U> threadJob) {

        //distribute the data
        final List<List<T>> threadJobs = equallyDistribute(elements, THREAD_NO);

        //distribute date on threads
        final List<Future<U>> futures = new ArrayList<>();
        threadJobs.forEach(job -> {
            futures.add(
                    executorService.submit(() -> threadJob.apply(job))
            );
        });

        return futures;
    }

    public <T> List<List<T>> equallyDistribute(final List<T> elements, final int threadNo) {
        final List<List<T>> batches = new ArrayList<>();

        //calculate the batch size
        final int N = elements.size();

        //equally distribute data
        int rest = N % threadNo;
        final int batchSize = N / threadNo;

        for (int thread = 0, start = 0; thread < threadNo; ++thread) {
            //rest cannot be negative
            rest = Math.max(rest, 0);

            //compute the right bound of the list [start, end) and check
            //check fot out of bounds
            final int end = start + batchSize + (rest-- > 0 ? 1 : 0);
            if (end > elements.size()) {
                return batches;
            }

            //add elements
            batches.add(elements.subList(start, (start = end)));
        }

        return batches;
    }

    public void stopExecutor() {
        executorService.shutdown();
    }
}
