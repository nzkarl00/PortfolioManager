let
  sources = import ./nix/sources.nix;
  frameworks = nixpkgs.darwin.apple_sdk.frameworks;

  nixpkgs = import sources.nixpkgs { overlays = []; };
in
  with nixpkgs;
stdenv.mkDerivation {
  allowBroken = true;
  name = "seng302-shell";
  shellHook = ''
    export JAVA_HOME="${pkgs.jdk17}/zulu-17.jdk/Contents/Home"
  '';
  buildInputs = [
    # General
    # niv
    git-lfs
    hexyl
    hyperfine
    # jq
    # pixman
    # pwgen
    tokei
    # yq

    jdk17
    gradle
  ]
  ++ lib.optional stdenv.isLinux [
  ]
  ++ lib.optional stdenv.isDarwin [
    frameworks.Security
    frameworks.CoreFoundation
    frameworks.CoreServices
  ];
}
