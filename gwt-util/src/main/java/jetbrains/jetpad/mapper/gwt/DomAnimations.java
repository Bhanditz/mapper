package jetbrains.jetpad.mapper.gwt;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import jetbrains.jetpad.base.animation.Animation;

import static com.google.gwt.query.client.GQuery.$;

public class DomAnimations {
  public static Animation fadeIn(final Element e, final int duration) {
    return new GQueryBasedAnimation() {
      @Override
      protected GQuery createAnimation(final Runnable callback) {
        return $(e).delay(50).hide().fadeIn(duration, new Function() {
          @Override
          public void f() {
            callback.run();
          }
        });
      }
    };
  }

  public static Animation fadeOut(final Element e, final int duration) {
    return new GQueryBasedAnimation() {
      @Override
      protected GQuery createAnimation(final Runnable callback) {
        return GQuery.$(e).css("opacity", "1").animate("opacity : 0", duration, new Function() {
          @Override
          public void f() {
            callback.run();
            $(e).css("opacity", "1");
          }
        });
      }
    };
  }

  public static Animation showSlide(final Element e, final int duration) {
    return new GQueryBasedAnimation() {
      @Override
      protected GQuery createAnimation(final Runnable callback) {
        return $(e).delay(50).hide().slideToggle(duration, new Function() {
          @Override
          public void f() {
            callback.run();
          }
        });
      }
    };
  }

  public static Animation hideSlide(final Element e, final int duration) {
    return new GQueryBasedAnimation() {
      @Override
      protected GQuery createAnimation(final Runnable callback) {
        int height = $(e).height();
        return $(e).height(height).animate("height : 0", duration, new Function() {
          @Override
          public void f() {
            $(e).height(null);
            callback.run();
          }
        });
      }
    };
  }
}
