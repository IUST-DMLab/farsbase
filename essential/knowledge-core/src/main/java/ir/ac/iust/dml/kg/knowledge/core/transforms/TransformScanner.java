package ir.ac.iust.dml.kg.knowledge.core.transforms;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Scan transformer in a base package
 */
public class TransformScanner {
    private static final Logger LOGGER = LogManager.getLogger(TransformScanner.class);
    private Map<String, ITransformer> transformers = new HashMap<>();
    private ITransformer generic = new GenericTransformer();

    public void scan(String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(true);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Transformer.class));

        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                final Transformer data = clazz.getAnnotation(Transformer.class);
                if (transformers.get(data.value()) != null)
                    LOGGER.error("Class:" + bd.getBeanClassName() + " repeated with title " + data.value());
                else if (ITransformer.class.isAssignableFrom(clazz)) {
                    final ITransformer transformer = (ITransformer) (clazz.newInstance());
                    transformers.put(data.value(), transformer);
                } else
                    LOGGER.error("Class:" + bd.getBeanClassName() + " not implement ITransformer");
            } catch (Throwable th) {
                LOGGER.error("Can not create class:" + bd.getBeanClassName());
            }
        }
    }

    public ITransformer getTransformer(String transformer) {
        final ITransformer t = transformers.get(transformer);
        if (t != null)
            return t;
        return generic;
    }

    public Set<String> getAvailableTransformer() {
        return transformers.keySet();
    }
}
