package com.parsleyj.tool.objects;

import com.parsleyj.tool.exceptions.AmbiguousMethodCallException;
import com.parsleyj.utils.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Giuseppe on 01/04/16.
 * TODO: javadoc
 */
public class MethodTable {
    private List<ToolMethod> methods = new ArrayList<>();

    public void add(ToolMethod method) {
        //todo: THIS IS TEMPORARY, must check ambiguous methods
        methods.add(0, method);
    }

    public List<ToolMethod> getCandidates(String name){
        return methods.stream().filter(m -> m.getName().equals(name)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ToolMethod resolve(String name, List<ToolClass> argumentTypes) throws AmbiguousMethodCallException {
        //step1: get all candidates (correct names, visible from call point(?))
        List<ToolMethod> candidates = getCandidates(name);

        /*step2: select viable functions among candidates.
                a viable function is a function that has types and number of arguments
                compatible with the the call. */
        //      Compatible means it can be converted (following some specific rules) in the other type
        List<ToolMethod> viables = getViableMethods(candidates, argumentTypes);

        //step3: choose the best function among the viable ones. cases:
        if(viables.isEmpty()) //1) there are no viable functions: throw MethodNotFoundException
            return null; //todo: throw exception directly from here?

        else if (viables.size() == 1) //2) there is 1 viable function: that's the one!
            return viables.get(0);

        else { //3) there are more than 1 functions: a rank system must be used and:
            List<Pair<Integer,List<ToolMethod>>> rankedMethods = getRankedMethods(candidates, argumentTypes);
            //                  3.1) there is one method at first place: that's the one!
            if(rankedMethods.get(0).getSecond().size() == 1){
                return rankedMethods.get(0).getSecond().get(0);

            }else{ //3.2) there are more than one method at first place: throw AmbiguousMethodCallException
                StringBuilder sb = new StringBuilder("Multiple method candidates found for call: "+name+"(");
                for (int i = 0; i < argumentTypes.size(); i++) {
                    ToolClass tc = argumentTypes.get(i);
                    sb.append(tc.getClassName());
                    if(i != argumentTypes.size() -1) sb.append(",");
                }
                sb.append("):\n");
                for (ToolMethod tm : rankedMethods.get(0).getSecond()) {
                    sb.append(tm).append("\n");
                }
                throw new AmbiguousMethodCallException(sb.toString());
            }
        }

        /* OLD METHOD FIND
        for (ToolMethod method : methods) {
            boolean found = true;
            List<ToolClass> methodArgumentTypes = method.getArgumentTypes();
            if (method.getName().equals(name) && (methodArgumentTypes.size() == argumentTypes.size())) {
                for (int i = 0; i < argumentTypes.size(); i++) {
                    ToolClass argumentType = argumentTypes.get(i);
                    ToolClass methodArgumentType = methodArgumentTypes.get(i);

                    Integer argumentTypeID1 = argumentType.getId();
                    Integer argumentTypeID2 = methodArgumentType.getId();
                    if (!Objects.equals(argumentTypeID1, argumentTypeID2) ){
                        found = false;
                        break;
                    }
                }
            } else {
                found = false;
            }
            if (found) return method;
        }
        return null;
        */
    }

    private List<Pair<Integer,List<ToolMethod>>> getRankedMethods(List<ToolMethod> candidates, List<ToolClass> argumentTypes) {
        List<Pair<Integer, List<ToolMethod>>> result = new ArrayList<>();
        for(ToolMethod cand : candidates){
            int total = 0;
            List<ToolClass> argumentTypes1 = cand.getArgumentTypes();
            for (int i = 0; i < argumentTypes1.size(); i++) {
                ToolClass candArgType = argumentTypes1.get(i);
                total += candArgType.getConvertibility(argumentTypes.get(i));
            }
            int place = 0;
            boolean newPlace = true;
            while (place < result.size()){
                if(total < result.get(place).getFirst()){
                    break;
                }else if(total == result.get(place).getFirst()){
                    newPlace = false;
                    break;
                }else ++place;
            }
            if(place >= result.size()){
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

    private List<ToolMethod> getViableMethods(List<ToolMethod> candidates, List<ToolClass> argumentTypes) {
        ArrayList<ToolMethod> result = new ArrayList<>();
        for(ToolMethod candidate: candidates){
            //todo check for special parameter definitions
            if(candidate.getArgumentTypes().size() != argumentTypes.size())
                continue;
            boolean isViable = true;
            for (int i = 0; i < argumentTypes.size(); i++) {
                ToolClass argumentType = argumentTypes.get(i);
                if (!argumentType.canBeConvertedTo(candidate.getArgumentTypes().get(i))){
                    isViable = false;
                    break;
                }
            }
            if(isViable) result.add(candidate);
        }
        return result;
    }
}
