with open('app/page.tsx', 'r', encoding='utf-8') as f:
    content = f.read()

bad = """    }
    animate();

    return ("""

good = """    }
    animate();

    return () => {
      cancelAnimationFrame(reqId);
      window.removeEventListener("scroll", updateScrollState);
      window.removeEventListener("resize", updateResponsiveLayout);
      renderer.dispose();
    };
  }, []);

  return ("""

if bad in content:
    content = content.replace(bad, good)
    with open('app/page.tsx', 'w', encoding='utf-8') as f:
        f.write(content)
    print("Fixed!")
else:
    print("Not found!")
