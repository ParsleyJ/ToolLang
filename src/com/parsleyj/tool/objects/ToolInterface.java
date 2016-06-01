package com.parsleyj.tool.objects;

import com.parsleyj.tool.memory.Memory;
import com.parsleyj.tool.objects.method.FormalParameter;
import com.parsleyj.tool.objects.method.ToolMethod;
import com.parsleyj.tool.objects.method.ToolMethodPrototype;
import com.parsleyj.tool.objects.method.Visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Giuseppe on 15/05/16.
 * TODO: javadoc
 */
public class ToolInterface extends ToolObject {

    private final String interfaceName;
    private final List<ToolInterface> parentInterfaces;
    private List<ToolMethodPrototype> instanceMethods = new ArrayList<>();

    public ToolInterface(Memory m, String interfaceName, List<ToolInterface> parentInterfaces) {
        super(m, m.baseTypes().C_INTERFACE);
        this.interfaceName = interfaceName;
        this.parentInterfaces = parentInterfaces;
    }


    public String getInterfaceName() {
        return interfaceName;
    }

    public List<ToolInterface> getParentInterfaces() {
        return parentInterfaces;
    }

    public List<ToolMethodPrototype> getDeclaredInstanceMethods() {
        return instanceMethods;
    }

    public void addParentInterface(ToolInterface toolInterface) {
        parentInterfaces.add(toolInterface);
    }

    public ToolInterface addMethodDeclaration(Memory m, String methodCategory, String name, FormalParameter[] formalParameters){
        this.instanceMethods.add(new ToolMethod(
                m,
                methodCategory,
                Visibility.Public,
                name,
                new FormalParameter[]{},
                formalParameters,
                memory -> memory.baseTypes().O_NULL
        ));
        return this;
    }

    public boolean isOrExtends(ToolInterface ti){
        if(ti == null) return false;
        if(Objects.equals(this.getId(), ti.getId())) return true;
        if(parentInterfaces.isEmpty()) return false;

        for(ToolInterface parent: parentInterfaces){
            if(parent.isOrExtends(ti)) return true;
        }
        return false;
    }

    public List<ToolMethodPrototype> getInstanceMethods(){
        List<ToolMethodPrototype> result = new ArrayList<>();
        result.addAll(instanceMethods);
        for(ToolInterface ti: parentInterfaces){
            result.addAll(ti.getInstanceMethods());
        }
        return result;
    }

}
