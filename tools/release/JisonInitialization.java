import java.util.Map;

/**
 * Run in a fresh JVM with the core runtime classpath. Reflection keeps this
 * diagnostic independent of internal parser types and publication versions.
 */
public final class JisonInitialization {
    public static void main(String[] args) throws Exception {
        String[] tables = {
            "agentflow.upstream.mermaid.AgentflowJisonTables",
            "block.upstream.mermaid.BlockJisonTables",
            "classdiagram.upstream.mermaid.ClassJisonTables",
            "erdiagram.upstream.mermaid.ErJisonTables",
            "flowchart.upstream.mermaid.FlowJisonTables",
            "gantt.upstream.mermaid.GanttJisonTables",
            "journey.upstream.mermaid.JourneyJisonTables",
            "mindmap.upstream.mermaid.MindmapJisonTables",
            "requirement.upstream.mermaid.RequirementJisonTables",
            "sequence.upstream.mermaid.SequenceJisonTables",
            "statediagram.upstream.mermaid.StateJisonTables",
            "xychart.upstream.mermaid.XyJisonTables"
        };
        int states = 0;
        int entries = 0;
        long started = System.nanoTime();
        for (String table : tables) {
            Class<?> type = Class.forName("com.swithun.cmpmermaid.core." + table);
            Object instance = type.getField("INSTANCE").get(null);
            Map<?, ?>[] rows = (Map<?, ?>[]) type.getMethod("getStates").invoke(instance);
            states += rows.length;
            for (Map<?, ?> row : rows) entries += row.size();
        }
        long elapsed = System.nanoTime() - started;
        if (states != 1611 || entries != 20857) throw new AssertionError("Table contents changed");
        System.out.printf("{\"initializationNanos\":%d,\"states\":%d,\"entries\":%d}%n",
                elapsed, states, entries);
    }
}
