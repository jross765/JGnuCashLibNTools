all : DOC


######################################################################

DOC : PICS \
      TEXTS


######################################################################

PICS : module-arch.png

TEXTS : select-securities.pdf


######################################################################
# PICS

module-arch.png : module-arch.pdf

module-arch.pdf : module-arch.odg


######################################################################
# TEXTS

select-securities.pdf : select-securities.md
	pandoc -f markdown -t pdf -i $< -o $@


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
