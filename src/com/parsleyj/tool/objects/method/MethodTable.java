package com.parsleyj.tool.objects.method;

import com.parsleyj.tool.exceptions.AmbiguousMethodCallException;
import com.parsleyj.tool.exceptions.AmbiguousMethodDefinitionException;
import com.parsleyj.tool.exceptions.MethodNotFoundException;
import com.parsleyj.tool.exceptions.ToolNativeException;
import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.ToolObject;
import com.parsleyj.tool.objects.ToolOptional;
import com.parsleyj.tool.objects.ToolType;
import com.parsleyj.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class MethodTable {
    private List<ToolMethod> methods = new ArrayList<>();
    private Memory mem;

    public MethodTable(Memory mem) {
        this.mem = mem;
    }

    public void add(ToolMethod method) throws AmbiguousMethodDefinitionException {
        if (methods.stream().noneMatch(m -> areAmbiguous(m, method)))
            methods.add(0, method);
        else throw new AmbiguousMethodDefinitionException(mem, "Method " + method + " is already defined.");
    }

    public void addAll(Collection<ToolMethod> methods) throws AmbiguousMethodDefinitionException {
        for (ToolMethod method : methods) {
            add(method);
        }
    }

    public void addAsExtension(ToolMethod method) {
        List<ToolMethod> newMethods = methods.stream().filter(method1 -> !areAmbiguous(method, method1)).collect(Collectors.toList());
        newMethods.add(method);
        this.methods = newMethods;
    }

    public void addAllAsExtension(Collection<ToolMethod> methods) {
        methods.forEach(this::addAsExtension);
    }

    public List<ToolMethod> getCandidates(String category, String name) {
        return methods.stream()
                .filter(m -> m.getMethodName().equals(name) && m.getMethodCategory().equals(category))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static boolean areAmbiguous(ToolMethod a, ToolMethod b) {
        //TODO: implement
        return false;
    }

    public List<ToolMethod> getResolvedMethods(String category, String name, List<ToolObject> arguments) throws ToolNativeException {
        //step1: get all candidates (correct names, visible from call point(?))
        List<ToolMethod> candidates = getCandidates(category, name);

        /*step2: select viable functions among candidates.
                a viable function is a function that has types and number of arguments
                compatible with the the call. */
        //      Compatible means it can be converted (following some specific rules) in the other type
        List<ToolMethod> viables = getViableMethods(candidates, arguments);

        //step3: choose the best function among the viable ones. cases:
        if (viables.size() <= 1) //2) there are 0 or 1 viable functions: return them
            return viables;

        else { //3) there is more than one function: a rank system must be used and the first places are returned:
            List<Pair<Integer, List<ToolMethod>>> rankedMethods = getRankedMethods(viables, (pt, i) -> pt.getObjectConvertibility(arguments.get(i)));
            return rankedMethods.get(0).getSecond();
        }
    }

    private List<ToolMethod> getViableMethodsByTypes(List<ToolMethod> candidates, List<ToolType> argumentTypes) throws ToolNativeException {
        ArrayList<ToolMethod> result = new ArrayList<>();
        for (ToolMethod candidate : candidates) {
            //todo check for special parameter definitions (defaults, varargs...)
            if (candidate.getArgumentTypes().size() != argumentTypes.size())
                continue;
            boolean isViable = true;
            for (int i = 0; i < argumentTypes.size(); i++) {
                ToolType argumentType = argumentTypes.get(i);
                if (!argumentType.canBeUsedAs(candidate.getArgumentTypes().get(i))) {
                    isViable = false;
                    break;
                }
            }
            if (isViable) result.add(candidate);
        }
        return result;

    }

    private List<ToolMethod> getResolvedMethodsByTypes(String category,
                                                       String name,
                                                       List<ToolType> argumentTypes) throws ToolNativeException {
        List<ToolMethod> viables = getViableMethodsByTypes(getCandidates(category, name), argumentTypes);
        if(viables.size() <= 1)
            return viables;
        else{
            List<Pair<Integer, List<ToolMethod>>> rankedMethods = getRankedMethods(viables,
                    (pt, i) -> pt.getConvertibility(argumentTypes.get(i)));
            return rankedMethods.get(0).getSecond();
        }
    }


    @FunctionalInterface
    interface OverloadResolutionParameterCriteria {
        int getPoints(ToolType formalParameterType, int parameterIndex) throws ToolNativeException;
    }


    @NotNull
    public ToolMethod resolve(
            ToolObject caller,
            String category,
            String name,
            List<ToolObject> arguments) throws ToolNativeException {

        List<ToolMethod> rankedMethods = getResolvedMethods(category, name, arguments);
        //3.1) there is one method at first place: that's the one!
        if(rankedMethods.isEmpty()) { //1) there are no viable functions: throw MethodNotFoundException
            throw new MethodNotFoundException(mem,
                    MethodNotFoundException.getDefaultMessage(category, caller, name, arguments));
        }

        if (rankedMethods.size() == 1) {
            return rankedMethods.get(0);

        } else { //3.2) there are more than one method at first place: throw AmbiguousMethodCallException
            StringBuilder sb = new StringBuilder("Multiple method candidates found for call: " + name + "(");
            for (int i = 0; i < arguments.size(); i++) {
                ToolObject toolObject = arguments.get(i);
                sb.append(toolObject);
                if (i != arguments.size() - 1) sb.append(",");
            }
            sb.append("):\n");
            for (ToolMethod tm : rankedMethods) {
                sb.append(tm).append("\n");
            }
            throw new AmbiguousMethodCallException(mem, sb.toString());
        }

    }

    public ToolOptional<ToolMethod> resolveByTypes(
            String category,
            String name,
            List<ToolType> argumentsTypes) throws ToolNativeException {
        List<ToolMethod> rankedMethods = getResolvedMethodsByTypes(category, name, argumentsTypes);

        if(rankedMethods.isEmpty()) {
            return new ToolOptional<>(mem);
        }

        if (rankedMethods.size() == 1) {
            return new ToolOptional<>(mem, rankedMethods.get(0));
        } else {
            throw new AmbiguousMethodCallException(mem, "Multiple methods found"); //todo specify
        }
    }



    private static List<Pair<Integer, List<ToolMethod>>> getRankedMethods(
            List<ToolMethod> candidates,
            OverloadResolutionParameterCriteria criteria) throws ToolNativeException {

        List<Pair<Integer, List<ToolMethod>>> result = new ArrayList<>();
        for (ToolMethod cand : candidates) {
            int total = 0;
            List<ToolType> argumentTypes1 = cand.getArgumentTypes();
            for (int i = 0; i < argumentTypes1.size(); i++) {
                ToolType candArgType = argumentTypes1.get(i);
                int candParConvertibility = criteria.getPoints(candArgType, i);
                total = (total+candParConvertibility<0)?(Integer.MAX_VALUE):(total+candParConvertibility);
            }
            int place = 0;
            boolean newPlace = true;
            while (place < result.size()) {
                if (total < result.get(place).getFirst()) {
                    break;
                } else if (total == result.get(place).getFirst()) {
                    newPlace = false;
                    break;
                } else ++place;
            }
            if (place >= result.size()) {
                ArrayList<ToolMethod> tmpList = new ArrayList<>();
                tmpList.add(cand);
                result.add(new Pair<>(total, tmpList));
            } else {
                if (newPlace) {
                    ArrayList<ToolMethod> tmpList = new ArrayList<>();
                    tmpList.add(cand);
                    result.add(place, new Pair<>(total, tmpList));
                } else {
                    result.get(place).getSecond().add(cand);
                }
            }
        }
        return result;
    }

    private static List<ToolMethod> getViableMethods(List<ToolMethod> candidates, List<ToolObject> arguments) throws ToolNativeException {
        ArrayList<ToolMethod> result = new ArrayList<>();
        for (ToolMethod candidate : candidates) {
            //todo check for special parameter definitions (defaults, varargs...)
            if (candidate.getArgumentTypes().size() != arguments.size())
                continue;
            boolean isViable = true;
            for (int i = 0; i < arguments.size(); i++) {
                ToolObject argument = arguments.get(i);
                if (!candidate.getArgumentTypes().get(i).isOperator(argument)) {
                    isViable = false;
                    break;
                }
            }
            if (isViable) result.add(candidate);
        }
        return result;
    }

    public MethodTable extend(MethodTable other) {
        MethodTable result = new MethodTable(mem);
        try {
            result.addAll(this.methods);
        } catch (AmbiguousMethodDefinitionException e) {
            //do nothing, no ambiguous definitions are here, because 'result' was empty
        }
        result.addAllAsExtension(other.methods);
        return result;
    }

    public int count() {
        return methods.size();
    }

    public boolean contains(String category, String name, List<ToolType> argumentsTypes) throws ToolNativeException {
        return !getResolvedMethodsByTypes(category, name, argumentsTypes).isEmpty();
    }

    public List<ToolMethod> getAll() {
        return methods;
    }

    public boolean isEmpty() {
        return methods.isEmpty();
    }
}
