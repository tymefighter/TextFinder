package com.interactiveDebugger;

import com.sun.jdi.*;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InteractiveDebugger {

    private Class<?> debugClass;
    private int[] breakPointLines;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            InteractiveDebugger debugger = new InteractiveDebugger();

            System.out.print("Enter class to debug: ");
            String className = scanner.nextLine();

            System.out.print("Enter breakpoints (comma-separated line numbers): ");
            String[] lines = scanner.nextLine().split(",");
            int[] breakpoints = Arrays.stream(lines).map(String::trim).mapToInt(Integer::parseInt).toArray();

            debugger.setDebugClass(Class.forName(className));
            debugger.setBreakPointLines(breakpoints);

            VirtualMachine vm = debugger.attachToRunningVM("localhost", "5005");
            System.out.println("Attached to JVM: " + vm.name());

            debugger.enableClassPrepareRequest(vm);
            vm.resume();

            debugger.processEvents(vm);

        } catch (Exception exception) {
            throw new RuntimeException("Failed to read user input", exception);
        }
    }

    public void setDebugClass(Class<?> debugClass) {
        this.debugClass = debugClass;
    }

    public void setBreakPointLines(int[] breakPointLines) {
        this.breakPointLines = breakPointLines;
    }

    public VirtualMachine attachToRunningVM(String hostname, String port) throws Exception {
        AttachingConnector connector = Bootstrap.virtualMachineManager().attachingConnectors().stream().filter(c -> c.name().equals("com.sun.jdi.SocketAttach")).findFirst().orElseThrow(() -> new RuntimeException("SocketAttach connector not found"));

        Map<String, Connector.Argument> args = connector.defaultArguments();
        args.get("hostname").setValue(hostname);
        args.get("port").setValue(port);

        return connector.attach(args);
    }

    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest request = vm.eventRequestManager().createClassPrepareRequest();
        request.addClassFilter(debugClass.getName());
        request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        request.enable();
    }

    public void processEvents(VirtualMachine vm) throws Exception {
        EventQueue queue = vm.eventQueue();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            EventSet eventSet = queue.remove();
            for (Event event : eventSet) {
                if (event instanceof ClassPrepareEvent classEvent) {
                    setBreakpoints(vm, classEvent.referenceType());

                } else if (event instanceof BreakpointEvent bpEvent) {
                    ThreadReference thread = bpEvent.thread();
                    StackFrame frame = thread.frame(0);

                    System.out.println("Breakpoint hit at: " + bpEvent.location().declaringType().name() + ":" + bpEvent.location().lineNumber());

                    List<LocalVariable> vars = frame.visibleVariables();
                    for (LocalVariable var : vars) {
                        Value value = frame.getValue(var);
                        System.out.println(var.name() + " = " + value);
                    }

                    boolean waiting = true;
                    while (waiting) {
                        System.out.print("Debugger command (step, next, out, continue, quit): ");
                        String command = scanner.nextLine().trim().toLowerCase();

                        switch (command) {
                            case "step":
                            case "next":
                            case "out":
                                int stepDepth = switch (command) {
                                    case "step" -> StepRequest.STEP_INTO;
                                    case "next" -> StepRequest.STEP_OVER;
                                    case "out" -> StepRequest.STEP_OUT;
                                    default -> throw new IllegalStateException("Unexpected value: " + command);
                                };

                                for (StepRequest req : vm.eventRequestManager().stepRequests()) {
                                    if (req.thread().equals(thread)) {
                                        vm.eventRequestManager().deleteEventRequest(req);
                                    }
                                }

                                StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                                        thread,
                                        StepRequest.STEP_LINE,
                                        stepDepth
                                );
                                stepRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                                stepRequest.enable();
                                waiting = false;
                                break;


                            case "continue":
                                waiting = false;
                                break;

                            case "quit":
                                vm.dispose();
                                running = false;
                                waiting = false;
                                break;

                            default:
                                System.out.println("Unknown command. Try: step, next, continue, quit");
                                break;
                        }
                    }

                } else if (event instanceof StepEvent stepEvent) {
                    ThreadReference thread = stepEvent.thread();
                    StackFrame frame = thread.frame(0);

                    System.out.println("Stepped to: " + stepEvent.location().declaringType().name() + ":" + stepEvent.location().lineNumber());

                    List<LocalVariable> vars = frame.visibleVariables();
                    for (LocalVariable var : vars) {
                        Value value = frame.getValue(var);
                        System.out.println("   " + var.name() + " = " + value);
                    }

                    vm.eventRequestManager().deleteEventRequest(stepEvent.request());

                } else if (event instanceof VMDisconnectEvent || event instanceof VMDeathEvent) {
                    System.out.println("Target VM has exited.");
                    running = false;
                }
            }
            eventSet.resume();
        }
    }


    private void setBreakpoints(VirtualMachine vm, ReferenceType refType) throws Exception {
        for (int line : breakPointLines) {
            List<Location> locations = refType.locationsOfLine(line);
            if (!locations.isEmpty()) {
                Location location = locations.getFirst();
                BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
                bpReq.setSuspendPolicy(EventRequest.SUSPEND_ALL);
                bpReq.enable();
                System.out.println("Breakpoint set at line: " + line);
            } else {
                System.out.println("No executable code at line: " + line);
            }
        }
    }
}

