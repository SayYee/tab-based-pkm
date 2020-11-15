import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.viz.ColorImpl;

import java.io.*;
import java.util.Date;

/**
 * @author SayYi
 */
public class GexfTest {

    public static void main(String[] args) {
        Gexf gexf = new GexfImpl();

        gexf.getMetadata()
                .setLastModified(new Date())
                .setCreator("sayyi")
                .setDescription("gexf-test");
        // 这个是设置Visualization的，启用之后，可以在node中使用viz:size之类的属性，颜色、位置、形状。
        // https://gephi.org/gexf/format/viz.html
        gexf.setVisualization(true);

        Graph graph = gexf.getGraph();
        graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);

        // 这个是设置每个节点的属性的。现在暂时没有需要，就不设置了。
//        AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
//        graph.getAttributeLists().add(attrList);

        // 创建节点
        Node myriel = graph.createNode("Myriel")
                .setLabel("Myriel")
                .setSize(28)
                .setColor(new ColorImpl(235, 81, 72));
        Node napoleon = graph.createNode("Napoleon")
                .setLabel("Napoleon")
                .setSize(28)
                .setColor(new ColorImpl(235, 81, 72));

        // 创建连线
        myriel.connectTo("0", napoleon);


        StaxGraphWriter graphWriter = new StaxGraphWriter();
        File f = new File("static_graph_sample.gexf");
        Writer out;
        try {
            out =  new MyWriter(f, false);
            graphWriter.writeToStream(gexf, out, "UTF-8");
            System.out.println(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MyWriter extends FileWriter {

        public MyWriter(File file, boolean append) throws IOException {
            super(file, append);
        }

        @Override
        public void flush() throws IOException {
            System.out.println("flush has been invoked");
            super.flush();
        }

        @Override
        public void close() throws IOException {
            System.out.println("close has been invoked");
            super.close();
        }
    }
}
