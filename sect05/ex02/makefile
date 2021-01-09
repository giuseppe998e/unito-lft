OS_ENV = /usr/bin/env
JAVAC  = javac
JAVA   = java
PDFTEX = pdflatex
MKDIR  = mkdir -p
ECHO   = echo
RM     = rm -rf

SRCDIR = src
OUTDIR = out
DOCDIR = doc

SRC := $(if $(SRC), $(SRC), "source.pas")

# --------------------------
# Run java application
.PHONY: run
run: $(OUTDIR)/Translator.class
	@$(SO_ENV) $(ECHO) "Running exercise..."
	@$(SO_ENV) $(JAVA) -cp $(OUTDIR) $(basename $(notdir $<)) $(SRC)
	@$(SO_ENV) $(JAVA) -jar ../libs/jasmin.jar Output.j &> /dev/null
	@$(SO_ENV) $(JAVA) Output

# --------------------------
# Build java class files
$(OUTDIR)/%.class: $(SRCDIR)/%.java
	@$(OS_ENV) $(ECHO) "Generating 'Java' class files..."
	@$(OS_ENV) $(MKDIR) $(OUTDIR)
	@$(OS_ENV) $(JAVAC) -cp $(SRCDIR) -d $(OUTDIR) $<

# --------------------------
# Build latex doc file
.PHONY: doc
doc:
	@$(SO_ENV) $(ECHO) "Generating 'LaTeX' pdf..."
	@$(SO_ENV) $(PDFTEX) -output-directory $(DOCDIR) $(DOCDIR)/sdt.tex &> /dev/null

# --------------------------
# Clean all generated classes
.PHONY: clean
clean:
	@$(OS_ENV) $(ECHO) "Cleaning 'Java' class files..."
	@$(OS_ENV) $(RM) $(OUTDIR)/*
	@$(OS_ENV) $(ECHO) "Cleaning 'LaTeX' doc files..."
	@$(OS_ENV) $(RM) $(DOCDIR)/*.aux $(DOCDIR)/*.fdb_latexmk $(DOCDIR)/*.fls 
	@$(OS_ENV) $(RM) $(DOCDIR)/*.pdf $(DOCDIR)/*.log $(DOCDIR)/*.synctex.gz
	@$(OS_ENV) $(ECHO) "Cleaning 'P' output files..."
	@$(OS_ENV) $(RM) *.j *.class