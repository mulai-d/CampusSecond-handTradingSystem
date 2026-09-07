import textwrap
from pathlib import Path

import matplotlib

matplotlib.use("Agg")

import matplotlib.pyplot as plt
from matplotlib import font_manager
from matplotlib.patches import FancyArrowPatch, FancyBboxPatch


FONT_PATH = r"C:\Windows\Fonts\msyh.ttc"
OUT_DIR = Path(__file__).resolve().parent

font_manager.fontManager.addfont(FONT_PATH)
FONT_NAME = font_manager.FontProperties(fname=FONT_PATH).get_name()
matplotlib.rcParams["font.family"] = FONT_NAME
matplotlib.rcParams["axes.unicode_minus"] = False

BLUE = "#E0F2FE"
BLUE_EDGE = "#0284C7"
YELLOW = "#FEF3C7"
YELLOW_EDGE = "#D97706"
GREEN = "#DCFCE7"
GREEN_EDGE = "#16A34A"
GRAY = "#F1F5F9"
GRAY_EDGE = "#64748B"


def prepare(ax, title=None):
    ax.set_xlim(0, 1)
    ax.set_ylim(-0.2, 1.05)
    ax.axis("off")
    if title:
        ax.set_title(title, fontsize=14, fontweight="bold", color="#0F172A", pad=12)


def draw_box(ax, x, y, w, h, text, fc=BLUE, ec=BLUE_EDGE, fs=10):
    patch = FancyBboxPatch(
        (x, y),
        w,
        h,
        boxstyle="round,pad=0.015,rounding_size=0.02",
        linewidth=1.3,
        edgecolor=ec,
        facecolor=fc,
        zorder=2,
    )
    ax.add_patch(patch)

    lines = []
    for segment in text.split("\n"):
        lines.extend(textwrap.wrap(segment, width=16))
    ax.text(
        x + w / 2,
        y + h / 2,
        "\n".join(lines),
        ha="center",
        va="center",
        fontsize=fs,
        color="#172554",
        linespacing=1.35,
        zorder=3,
    )


def draw_arrow(ax, p1, p2, label=None, color="#475569", dashed=False):
    arrow = FancyArrowPatch(
        p1,
        p2,
        arrowstyle="-|>",
        mutation_scale=14,
        lw=1.5,
        color=color,
        linestyle="--" if dashed else "-",
        connectionstyle="arc3,rad=0",
        zorder=3,
    )
    ax.add_patch(arrow)
    if label:
        mid = ((p1[0] + p2[0]) / 2, (p1[1] + p2[1]) / 2)
        ax.text(
            mid[0],
            mid[1] + 0.018,
            label,
            ha="center",
            va="bottom",
            fontsize=9,
            color="#2563EB",
            zorder=4,
        )


def draw_overview():
    fig, ax = plt.subplots(figsize=(12, 7))
    prepare(ax, "系统总体流程")

    ax.text(0.12, 0.98, "前端", ha="center", fontsize=13, fontweight="bold", color="#0284C7")
    ax.text(0.46, 0.98, "后端", ha="center", fontsize=13, fontweight="bold", color="#D97706")
    ax.text(0.85, 0.98, "存储与缓存", ha="center", fontsize=13, fontweight="bold", color="#16A34A")

    draw_box(ax, 0.02, 0.82, 0.22, 0.11, "用户浏览器")
    draw_box(ax, 0.02, 0.67, 0.22, 0.11, "Vue 3 + Vite")
    draw_box(ax, 0.02, 0.52, 0.22, 0.11, "Vue Router 页面")
    draw_box(ax, 0.02, 0.37, 0.22, 0.11, "API 请求封装")

    draw_box(ax, 0.35, 0.37, 0.24, 0.11, "Vite /api 代理", fc=YELLOW, ec=YELLOW_EDGE)
    draw_box(ax, 0.35, 0.22, 0.24, 0.11, "Spring Boot\nController")
    draw_box(ax, 0.35, 0.07, 0.24, 0.11, "Service 业务层")
    draw_box(ax, 0.35, -0.08, 0.24, 0.11, "MyBatis-Plus Mapper")

    draw_box(ax, 0.76, 0.52, 0.20, 0.11, "Redis 缓存", fc=GREEN, ec=GREEN_EDGE)
    draw_box(ax, 0.76, 0.22, 0.20, 0.11, "MySQL", fc=GREEN, ec=GREEN_EDGE)

    draw_arrow(ax, (0.13, 0.82), (0.13, 0.78))
    draw_arrow(ax, (0.13, 0.67), (0.13, 0.63))
    draw_arrow(ax, (0.13, 0.52), (0.13, 0.48))
    draw_arrow(ax, (0.24, 0.425), (0.35, 0.425), label="请求转发")
    draw_arrow(ax, (0.47, 0.37), (0.47, 0.33))
    draw_arrow(ax, (0.47, 0.22), (0.47, 0.18))
    draw_arrow(ax, (0.47, 0.07), (0.47, 0.03))

    draw_arrow(ax, (0.59, 0.125), (0.76, 0.575), label="读缓存")
    draw_arrow(ax, (0.59, 0.125), (0.59, 0.03))
    draw_arrow(ax, (0.59, -0.025), (0.76, 0.22), label="写缓存/读库")

    fig.savefig(OUT_DIR / "system-overview.png", dpi=180, bbox_inches="tight")
    plt.close(fig)


def draw_browse_detail():
    fig, axes = plt.subplots(
        2,
        1,
        figsize=(11, 12),
        gridspec_kw={"height_ratios": [1, 1.15]},
    )

    ax1, ax2 = axes
    prepare(ax1, "商品列表流程")

    draw_box(ax1, 0.35, 0.87, 0.30, 0.08, "进入系统")
    draw_box(ax1, 0.35, 0.73, 0.30, 0.08, "商品列表页 /")
    draw_box(ax1, 0.35, 0.59, 0.30, 0.08, "GET /products")
    draw_box(ax1, 0.35, 0.45, 0.30, 0.08, "Service.listProducts")
    draw_box(ax1, 0.33, 0.28, 0.34, 0.10, "Redis 列表缓存命中?", fc=YELLOW, ec=YELLOW_EDGE)
    draw_box(ax1, 0.03, 0.10, 0.24, 0.10, "返回缓存分页", fc=GREEN, ec=GREEN_EDGE)
    draw_box(ax1, 0.72, 0.10, 0.25, 0.10, "查 MySQL\n写 Redis 缓存")
    draw_box(ax1, 0.30, -0.04, 0.40, 0.08, "返回 PageResult")

    draw_arrow(ax1, (0.50, 0.87), (0.50, 0.81))
    draw_arrow(ax1, (0.50, 0.73), (0.50, 0.67))
    draw_arrow(ax1, (0.50, 0.59), (0.50, 0.53))
    draw_arrow(ax1, (0.50, 0.45), (0.50, 0.38))
    draw_arrow(ax1, (0.33, 0.33), (0.15, 0.20), label="命中")
    draw_arrow(ax1, (0.67, 0.33), (0.845, 0.20), label="未命中")
    draw_arrow(ax1, (0.15, 0.10), (0.30, 0.04))
    draw_arrow(ax1, (0.845, 0.10), (0.70, 0.04))

    prepare(ax2, "商品详情流程")

    draw_box(ax2, 0.35, 0.88, 0.30, 0.08, "点击商品卡片")
    draw_box(ax2, 0.35, 0.74, 0.30, 0.08, "详情页 /products/:id")
    draw_box(ax2, 0.35, 0.60, 0.30, 0.08, "GET /products/:id")
    draw_box(ax2, 0.35, 0.46, 0.30, 0.08, "Service.viewProduct")
    draw_box(ax2, 0.33, 0.29, 0.34, 0.10, "Redis 详情缓存命中?", fc=YELLOW, ec=YELLOW_EDGE)
    draw_box(ax2, 0.02, 0.10, 0.27, 0.10, "读取缓存商品", fc=GREEN, ec=GREEN_EDGE)
    draw_box(ax2, 0.69, 0.10, 0.29, 0.10, "查 MySQL\n浏览量自增\n更新 Redis")
    draw_box(ax2, 0.30, -0.04, 0.40, 0.08, "返回商品详情并渲染")

    draw_arrow(ax2, (0.50, 0.88), (0.50, 0.82))
    draw_arrow(ax2, (0.50, 0.74), (0.50, 0.68))
    draw_arrow(ax2, (0.50, 0.60), (0.50, 0.54))
    draw_arrow(ax2, (0.50, 0.46), (0.50, 0.39))
    draw_arrow(ax2, (0.33, 0.34), (0.155, 0.20), label="命中")
    draw_arrow(ax2, (0.67, 0.34), (0.835, 0.20), label="未命中")
    draw_arrow(ax2, (0.155, 0.10), (0.30, 0.04))
    draw_arrow(ax2, (0.835, 0.10), (0.70, 0.04))

    fig.savefig(OUT_DIR / "browse-detail-flow.png", dpi=180, bbox_inches="tight")
    plt.close(fig)


def draw_favorite_message():
    fig, axes = plt.subplots(1, 2, figsize=(14, 7))

    ax1, ax2 = axes
    prepare(ax1, "收藏流程")

    draw_box(ax1, 0.25, 0.86, 0.50, 0.08, "商品详情页")
    draw_box(ax1, 0.25, 0.72, 0.50, 0.08, "点击收藏按钮")
    draw_box(ax1, 0.25, 0.58, 0.50, 0.08, "查询当前收藏状态")
    draw_box(ax1, 0.22, 0.40, 0.56, 0.10, "是否已收藏?", fc=YELLOW, ec=YELLOW_EDGE)
    draw_box(ax1, 0.02, 0.23, 0.31, 0.10, "POST\n/favorites/product/:id", fc=GREEN, ec=GREEN_EDGE)
    draw_box(ax1, 0.67, 0.23, 0.31, 0.10, "DELETE\n/favorites/product/:id")
    draw_box(ax1, 0.25, 0.07, 0.50, 0.10, "写入或删除 favorite")
    draw_box(ax1, 0.25, -0.09, 0.50, 0.10, "删除该用户收藏缓存并更新按钮")

    draw_arrow(ax1, (0.50, 0.86), (0.50, 0.80))
    draw_arrow(ax1, (0.50, 0.72), (0.50, 0.66))
    draw_arrow(ax1, (0.50, 0.58), (0.50, 0.50))
    draw_arrow(ax1, (0.22, 0.45), (0.175, 0.33), label="否")
    draw_arrow(ax1, (0.78, 0.45), (0.825, 0.33), label="是")
    draw_arrow(ax1, (0.175, 0.23), (0.30, 0.17))
    draw_arrow(ax1, (0.825, 0.23), (0.70, 0.17))
    draw_arrow(ax1, (0.50, 0.07), (0.50, 0.01))

    prepare(ax2, "留言流程")

    draw_box(ax2, 0.25, 0.88, 0.50, 0.08, "商品详情页")
    draw_box(ax2, 0.25, 0.74, 0.50, 0.08, "输入留言或点击回复")
    draw_box(ax2, 0.25, 0.60, 0.50, 0.08, "POST /messages/product/:id")
    draw_box(ax2, 0.25, 0.46, 0.50, 0.08, "校验内容和 parentId")
    draw_box(ax2, 0.25, 0.32, 0.50, 0.08, "保存 message")
    draw_box(ax2, 0.25, 0.18, 0.50, 0.08, "删除该商品留言缓存")
    draw_box(ax2, 0.25, 0.04, 0.50, 0.08, "重新加载留言列表")

    draw_arrow(ax2, (0.50, 0.88), (0.50, 0.82))
    draw_arrow(ax2, (0.50, 0.74), (0.50, 0.68))
    draw_arrow(ax2, (0.50, 0.60), (0.50, 0.54))
    draw_arrow(ax2, (0.50, 0.46), (0.50, 0.40))
    draw_arrow(ax2, (0.50, 0.32), (0.50, 0.26))
    draw_arrow(ax2, (0.50, 0.18), (0.50, 0.12))

    fig.savefig(OUT_DIR / "favorite-message-flow.png", dpi=180, bbox_inches="tight")
    plt.close(fig)


if __name__ == "__main__":
    draw_overview()
    draw_browse_detail()
    draw_favorite_message()
    print("flowcharts generated in", OUT_DIR)
