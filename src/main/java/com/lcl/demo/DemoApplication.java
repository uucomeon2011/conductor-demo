package com.lcl.demo;

import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.task.WorkflowTaskCoordinator;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
    public static void main(String[] args) {
        logger.debug("开始轮询任务");
        TaskClient taskClient = new TaskClient();
        taskClient.setRootURI("http://localhost:8080/api/");       //Point this to the server API

        int threadCount = 2;         //number of threads used to execute workers.  To avoid starvation, should be same or more than number of workers

        Worker worker1 = new SampleWorker("mytask1");
        Worker worker2 = new SampleWorker2("mytask3");

        //Create WorkflowTaskCoordinator
        WorkflowTaskCoordinator.Builder builder = new WorkflowTaskCoordinator.Builder();
        WorkflowTaskCoordinator coordinator = builder.withWorkers(worker1,worker2).withThreadCount(threadCount).withTaskClient(taskClient).build();

        coordinator.init();
    }


    static class SampleWorker implements Worker {

        private String taskDefName;

        public SampleWorker(String taskDefName) {
            this.taskDefName = taskDefName;
        }

        @Override
        public String getTaskDefName() {
            return taskDefName;
        }

        @Override
        public TaskResult execute(Task task) {

            System.out.printf("Executing %s\n", taskDefName);
            System.out.println("ai1:" + task.getInputData().get("ai1"));
            System.out.println("ai2:" + task.getInputData().get("ai2"));
            TaskResult result = new TaskResult(task);
            result.setStatus(TaskResult.Status.COMPLETED);

            //Register the output of the task
            result.getOutputData().put("ao1", String.valueOf(task.getInputData().get("ai1")) + " from ai1");
            result.getOutputData().put("ao2", String.valueOf(task.getInputData().get("ai2")) + " from ai2");

            return result;
        }
    }

    static class SampleWorker2 implements Worker {

        private String taskDefName;

        public SampleWorker2(String taskDefName) {
            this.taskDefName = taskDefName;
        }

        @Override
        public String getTaskDefName() {
            return taskDefName;
        }

        @Override
        public TaskResult execute(Task task) {

            System.out.printf("Executing %s\n", taskDefName);
            System.out.println("bi1:" + task.getInputData().get("ao1"));
            System.out.println("bi2:" + task.getInputData().get("ao2"));
            TaskResult result = new TaskResult(task);
            result.setStatus(TaskResult.Status.COMPLETED);

            //Register the output of the task
            result.getOutputData().put("bo1", String.valueOf(task.getInputData().get("ao1")) + " from bi1");
            result.getOutputData().put("bo2", String.valueOf(task.getInputData().get("ao2")) + " from bi2");

            return result;
        }

    }
}
