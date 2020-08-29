package pipeline;

/**
 *  * PipelineContext"俗称“上下文”，
 *  * 代表从开始贯穿到结尾的一个执行环境，
 *  * 用于在各个Pipeline之间传递信息；
 *  * 一个“Pipeline”包含两个方法，
 *  * process代表当前的处理流程，
 *  * forward方法代表将处理消息转发给下游的流程：
 *  * 上游可以控制消息是否转发给下游。
 * @author liufei
 * @since 2020-01-21
 */
public interface Pipeline<T> {

  /**
   * 当前处理流程
   * @param ctx
   * @param t
   */
  void process(PipelineContext ctx, T t);

  /**
   * 下游流程
   * @param ctx
   * @param t
   */
  void forward(PipelineContext ctx, T t);
}