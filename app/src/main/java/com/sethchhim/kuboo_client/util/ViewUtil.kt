package com.sethchhim.kuboo_client.util

import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView

class ViewUtil {

    fun fadeInViewGroup(viewGroup: ViewGroup?) {
        if (viewGroup != null && !viewGroup.isShown) {
            viewGroup.alpha = 1f
            viewGroup.visibility = View.VISIBLE

            val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
            fadeInAnimation.duration = 300
            viewGroup.startAnimation(fadeInAnimation)
        }
    }

    fun fadeOutViewGroup(viewGroup: ViewGroup?) {
        if (viewGroup != null && viewGroup.isShown) {
            val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
            fadeOutAnimation.duration = 300
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    viewGroup.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            viewGroup.startAnimation(fadeOutAnimation)
        }
    }

    fun fadeInvisible(imageView: ImageView?) {
        if (imageView != null && imageView.isShown) {
            val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
            fadeOutAnimation.duration = 300
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    imageView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            imageView.startAnimation(fadeOutAnimation)
        }
    }

    fun fadeInView(view: View?) {
        if (view != null && !view.isShown) {
            view.alpha = 1.0f
            view.visibility = View.VISIBLE

            val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
            fadeInAnimation.duration = 400
            view.startAnimation(fadeInAnimation)
        }
    }

    fun fadeInView(view: View?, duration: Int) {
        if (view != null && !view.isShown) {
            view.alpha = 1.0f
            view.visibility = View.VISIBLE

            val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
            fadeInAnimation.duration = duration.toLong()
            view.startAnimation(fadeInAnimation)
        }
    }

    fun fadeInView(view: View?, duration: Int, delay: Int) {
        if (view != null && !view.isShown) {
            view.alpha = 1.0f
            view.visibility = View.VISIBLE

            val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
            fadeInAnimation.duration = duration.toLong()
            view.postDelayed({
                view.startAnimation(fadeInAnimation)
            }, delay.toLong())
        }
    }

    fun fadeOutView(view: View?, duration: Int) {
        if (view != null && view.isShown) {
            val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
            fadeOutAnimation.duration = duration.toLong()
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            view.startAnimation(fadeOutAnimation)
        }
    }

    fun fadeOutView(view: View?) {
        if (view != null && view.isShown) {
            val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
            fadeOutAnimation.duration = 400
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            view.startAnimation(fadeOutAnimation)
        }
    }

    fun fadeInMask(imageMask: View?) {
        if (imageMask != null && !imageMask.isShown) {
            imageMask.bringToFront()
            imageMask.visibility = View.VISIBLE
            val fadeInAnimation = AlphaAnimation(0.0f, 0.8f)
            fadeInAnimation.duration = 400
            fadeInAnimation.fillAfter = true
            imageMask.startAnimation(fadeInAnimation)
        }
    }

    fun fadeOutMask(imageMask: View?) {
        if (imageMask != null && imageMask.isShown) {
            val fadeInAnimation = AlphaAnimation(0.8f, 0.0f)
            fadeInAnimation.duration = 300
            fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    imageMask.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            imageMask.startAnimation(fadeInAnimation)
        }
    }

    fun fadeOutFabProgress(view: View?, delay: Long) {
        Handler().postDelayed({
            if (view != null && view.isShown) {
                val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
                fadeOutAnimation.duration = 700
                fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        view.visibility = View.INVISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
                view.startAnimation(fadeOutAnimation)
            }
        }, delay)
    }

    fun rotateFabOriginal(fab: FloatingActionButton) {
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(fab).rotation(0f).withLayer().setDuration(300).setInterpolator(interpolator).start()
    }

    fun rotateFabLoading(fab: FloatingActionButton) {
        val interpolator = OvershootInterpolator()
        ViewCompat.animate(fab).rotation(135f).withLayer().setDuration(300).setInterpolator(interpolator).start()
    }

    fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    fun shrinkView(view: View) {
        val shrinkAnim = ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        val growAnim = ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        growAnim.duration = 300
        shrinkAnim.duration = 300
        shrinkAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                view.startAnimation(growAnim)

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        view.startAnimation(shrinkAnim)
    }

}