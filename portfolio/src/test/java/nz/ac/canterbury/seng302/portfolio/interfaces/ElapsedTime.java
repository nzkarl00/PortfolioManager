package nz.ac.canterbury.seng302.portfolio.interfaces;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ElapsedTime {
}
