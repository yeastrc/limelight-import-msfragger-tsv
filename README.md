Native MSFragger limelight XML Converter
==========================================

Use this program to convert the results of a MSFragger analysis to
limelight XML suitable for import into the limelight web application.

Note:
- Uses the native tab delimited (TSV) output of MSFragger
- If doing an open mod search, be sure to use --open-mod

How To Run
-------------
1. Download the [latest release](https://github.com/yeastrc/limelight-import-msfragger-tsv/releases).
2. Run the program ``java -jar msFragger2LimelightXML.jar`` with no arguments to see the possible parameters. Requires Java 8 or higher.

Command line documentation
---------------------------

```
java -jar msFragger2LimelightXML.jar [-hvV] [--open-mod] -f=<fastaFile>
                                     -m=<tsvFile> -o=<outFile>
                                     -p=<msFraggerConfFile>

Description:

Convert the results of a MSFragger analysis to a Limelight XML file suitable
for import into Limelight.

More info at: https://github.com/yeastrc/limelight-import-msfragger-tsv

Options:
  -p, --msfragger-params=<msFraggerConfFile>
                             Path to MSFragger .params file
  -f, --fasta-file=<fastaFile>
                             Full path to FASTA file used in the experiment. E.g.,
                               /data/yeast.fa
  -m, --msfragger-tsv=<tsvFile>
                             Path to TSV file generated by MSFragger
  -o, --out-file=<outFile>   Full path to use for the Limelight XML output file. E.
                               g., /data/my_analysis/crux.limelight.xml
  -v, --verbose              If this parameter is present, error messages will
                               include a full stacktrace. Helpful for debugging.
      --open-mod             If this parameter is present, the converter will run in
                               open mod mode. Mass diffs on the PSMs will be treated
                               as an unlocalized modification mass for the peptide.
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```