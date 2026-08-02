package plugins.UML2VDM;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

public class XMIOperation {
    
    public enum OpTypes {operation, function}
    
    private String signature;
    private OpTypes opType;
    private String shortName;
    private String visibility;

    public XMIOperation(Element aElement)
    {
        String xmiName = (aElement.getAttribute("name"));

		if (xmiName.contains(" «function»"))
        {
            this.opType = OpTypes.function;
            xmiName = xmiName.replace(" «function»", "");
        }	
        else
		{
            this.opType = OpTypes.operation;
		}

		this.visibility = visibility(aElement);

		// +A() : A
		// +A(nat) : A
		// +op()
		// +op(nat)
		// +op(nat, nat)
		// -f() : nat <<function>>
		// -f(nat) : nat <<function>>
		// -f(nat, nat) : nat <<function>>

		Pattern P = Pattern.compile("(\\w+)\\((.*)\\)(\\s*:\\s*(.+))?");
		Matcher m = P.matcher(xmiName);

		if (m.matches())
		{
			String name = m.group(1);
			String ptypes = m.group(2);
			String rtype = m.group(4);

			if (rtype == null) rtype = "()";

			// Turn "nat, char, int" into "a, b, c" and "nat * char * bool"
			String pnames = "";

			if (!ptypes.isEmpty())
			{
				String[] parts = ptypes.split("\\s*,\\s*");
				char arg = 'a';
				String psep = "";
				String tsep = "";
				String types = "";

				for (int a=0; a < parts.length; a++)
				{
					pnames = pnames + psep + arg;
					types  = types  + tsep + parts[a];
					psep = ", ";
					tsep = " * ";
					arg = (char)(arg + 1);	// a, b, c etc...
				}

				ptypes = types;
			}
			else
			{
				ptypes = "()";
			}

			if (opType == OpTypes.function)
			{
				this.signature = name + ": " + ptypes + " -> " + rtype;
				this.shortName = name + "("  + pnames + ") == is not yet specified;";
			}
			else
			{
				this.signature = name + ": " + ptypes + " ==> " + rtype;
				this.shortName = name + "("  + pnames + ") == is not yet specified;";
			}
		}
    }
    
    private String visibility(Element element)
	{
		if (element.getAttribute("visibility").contains("private")) 
			return "private ";
	
		else if (element.getAttribute("visibility").contains("public"))
            return "public ";
            
        else if (element.getAttribute("visibility").contains("protected"))
            return "protected "; 

        else return "private ";
	}

    public String getVisibility()
    {
        return visibility;
    }

    public String getSignature()
    {
        return signature;
    }

    public String getShortName()
    {
        return this.shortName;
    }

    public OpTypes getOpType()
    {
        return opType;
    }
}
    

